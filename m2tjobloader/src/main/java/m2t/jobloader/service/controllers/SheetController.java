package m2t.jobloader.service.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.standard.util.StandardConditionalCommentUtils.ConditionalCommentParsingResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;

import m2t.jobloader.configuration.Configuration;
import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.Container;
import m2t.jobloader.dao.model.Job;
import m2t.jobloader.dao.model.translators.JobTranslator;
import m2t.jobloader.dao.repositories.ClientRepository;
import m2t.jobloader.dao.repositories.ContainerRepository;
import m2t.jobloader.dao.repositories.JobRepository;
import m2t.jobloader.reports.factory.BasicBatchRequestFactory;
import m2t.jobloader.reports.factory.ClientReportFactory;
import m2t.jobloader.reports.factory.MasterExcelFactory;
import m2t.jobloader.reports.factory.MasterReportFactory;
import m2t.jobloader.reports.factory.ReportFactoryResponse;
import m2t.jobloader.service.controllers.model.BasicServiceResponse;
import m2t.jobloader.service.controllers.model.CreateReportResponse;
import m2t.jobloader.service.controllers.model.JobUpdate;
import m2t.jobloader.service.controllers.model.ResponseErrorDetail;
import m2t.jobloader.service.controllers.model.SheetServiceContainerData;
import m2t.jobloader.service.controllers.model.SheetServiceResponse;
import m2t.service.model.jobloader.JobDTO;
import m2t.service.model.reports.ClientReportDTO;

@RestController
public class SheetController {

	@Autowired
	ClientRepository clientRepository;
	@Autowired
	JobRepository jobRepository;
	@Autowired
	ContainerRepository containerRepository;
	@Autowired
	Configuration configuration;

	@Autowired
	GoogleWrapper wrapper;
	@Autowired
	JobLoaderService jobLoaderService;

	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	MasterExcelFactory masterExcelFactory;

	public GoogleWrapper getWrapper() {
		return wrapper;
	}

	public void setWrapper(GoogleWrapper wrapper) {
		this.wrapper = wrapper;
	}

	@RequestMapping(path = "jobloader/sheet/{containerNumber}/create")
	@ResponseBody
	public BasicServiceResponse createSheet(@PathVariable("containerNumber") String containerNumber) {
//		
		SheetServiceResponse response = new SheetServiceResponse("create sheet");
		Container container = containerRepository.findByContainerNumber(containerNumber);
		if (container == null) {
			response.getWarnings().add(
					new ResponseErrorDetail("Warning", "The container " + containerNumber + " is not in the db", ""));
			response.setFound(0);
			response.setError(true);
			return response;
		}
		response.setFound(response.getFound() + 1);
		if (container.getSheetId() == null) {
			String sheetId;
			SheetServiceContainerData containerResponse;
			try {
				containerResponse = duplicateFromTemplate(containerNumber, response);
			} catch (IOException | GeneralSecurityException e2) {
				response.setError(true);
				response.setErrorDescription("Error while duplicating the template ");
				response.getWarnings().add(
						new ResponseErrorDetail("Error", e2.getMessage(), "Container number :" + containerNumber, e2));
				return response;
			}
			sheetId = containerResponse.getSheetId();
			container.setSheetId(sheetId);
			container.setFullURL(containerResponse.getSheetFullURL());
			List<Job> jobs = jobRepository.findByContainerOrderByTotalBoxesDesc(containerNumber);
//			List<JobDTO> dtos = jobs.stream().map(job ->{
//															JobTranslator jt = new JobTranslator();
//															return jt.toDTO(job);
//														}).collect(Collectors.toList());
//			MasterReportFactory masterReportFactory = applicationContext.getBean(MasterReportFactory.class);
//			
////			masterReportFactory.setSheetId(sheetId);
////			masterReportFactory.setSheetURL(container.getFullURL());
//			ReportFactoryResponse reporFactoryResponse = masterReportFactory.buildReport(dtos, dto -> {return true;}, Comparator.comparing(JobDTO::getTotalBoxes));
//			container.setSheetId(masterReportFactory.getSheetId());
//			container.setFullURL(masterReportFactory.getSheetURL());
//			response.addOperationResponse(reporFactoryResponse);
			try {
				writeJobsOnSpreadSheet(sheetId, jobs, response);
			} catch (IOException | GeneralSecurityException e) {
				response.setError(true);
				response.setErrorDescription("Error while updating the jobs in the template " + sheetId);

				response.getWarnings().add(new ResponseErrorDetail("Error", e.getMessage(), jobs, e));
			}
//			try {
//				applyFilters(sheetId);
//			} catch (IOException | GeneralSecurityException e) {
//				response.getWarnings().add(new ResponseErrorDetail("ERROR", "Error while applying the filter", e.getMessage(), e));
//				return response;
//			}
			containerRepository.save(container);

		} else {
			response.getContainerResponse().add(
					new SheetServiceContainerData(container.getSheetId(), container.getFullURL(), containerNumber));
		}

		return response;
	}

	@RequestMapping("/reports/{containerNumber}/sheet")
	@ResponseBody
	public CreateReportResponse createReport(@PathVariable("containerNumber") String containerNumber) {

		CreateReportResponse response = jobLoaderService.extractContainerReportData(containerNumber);
		if (response.getFound() == 0 || response.isError()) {
			return response;
		}
		String sheetId;
		try {
			sheetId = duplicateFromClientReportTemplate(containerNumber, response);

			writeReport(sheetId, response);

			Container container = containerRepository.findByContainerNumber(containerNumber);
			container.setReportFullURL(response.getSheetFullURL());
			container.setReportSheetId(sheetId);
			containerRepository.save(container);
		} catch (IOException | GeneralSecurityException e) {
			response.setError(true);
			response.getWarnings().add(new ResponseErrorDetail("ERROR",
					"Error while duplicating the Client Report Template", e.getMessage(), e));
			return response;
		}

		return response;
	}

	@RequestMapping("/reports/{containerNumber}/download")
	public ResponseEntity<Resource> downloadReport(@PathVariable("containerNumber") String containerNumber) throws IOException {
		
		Container container = containerRepository.findByContainerNumber(containerNumber);
		InputStreamResource resource = null;
		if(container == null) {
			String errorMessage = "ERROR: the container " + containerNumber + " doesn't exist. Choose another container ";
			resource = new InputStreamResource(new ByteArrayInputStream(errorMessage.getBytes()));
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/html")).body(resource);
		}
		if(container.getReportSheetId() == null) {
		  CreateReportResponse createReportResponse = createReport(containerNumber);
		  if(createReportResponse.isError()) {
			  	String errorMessage = "ERROR: could not create report for " + containerNumber + createReportResponse.toString();
				resource = new InputStreamResource(new ByteArrayInputStream(errorMessage.getBytes()));
				return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/html")).body(resource);
		  }else {
			  container = containerRepository.findByContainerNumber(containerNumber); 
		  }
		}
		String sheetId = container.getReportSheetId();
		
		try {
			String mimeType = "application/pdf";
			InputStream is = getWrapper().getDriveService().files().export(sheetId,mimeType).set("portrait", Boolean.TRUE).set("scale","4").set("printtitle", "true").executeAsInputStream();
			resource = new InputStreamResource(is);

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType)).body(resource);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			String errorMessage = "ERROR:  report for " + containerNumber + e.getMessage();
			resource = new InputStreamResource(new ByteArrayInputStream(errorMessage.getBytes()));
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/html")).body(resource);
		}
	}

	private void applyFilters(String sheetId) throws IOException, GeneralSecurityException {
		List<Request> applyClavasFilters = masterExcelFactory.applyClavasFilters();
		wrapper.executeBatchUpdate(sheetId, applyClavasFilters);
	}

	private void writeReport(String sheetId, CreateReportResponse response) {

		List<Request> sheetsRequests = applicationContext.getBean(ClientReportFactory.class)
				.getCreateSheetsRequest(response.getClientReports());
		try {
			BatchUpdateSpreadsheetResponse result = wrapper.executeBatchUpdate(sheetId, sheetsRequests);
		} catch (IOException | GeneralSecurityException e) {
			response.setError(true);
			e.printStackTrace();
			response.getWarnings().add(new ResponseErrorDetail("ERROR",
					"Error creating the sheets for the clientReport", sheetsRequests, e));
			return;
		}
		List<Request> requests = new ArrayList<>();
		for (int i = 0; i < response.getClientReports().size(); i++) {
			ClientReportDTO report = response.getClientReports().get(i);
			ClientReportFactory clientReportFactory = applicationContext.getBean(ClientReportFactory.class);
			requests.addAll(report.isInstaller() ? clientReportFactory.getRequestForInstaller(report, i)
					: clientReportFactory.getRequestForDealer(report, i));
		}

		try {
			BatchUpdateSpreadsheetResponse result = wrapper.executeBatchUpdate(sheetId, requests);
		} catch (IOException | GeneralSecurityException e) {
			response.setError(true);
			e.printStackTrace();
			response.getWarnings()
					.add(new ResponseErrorDetail("ERROR", "Error generating the Client Report", requests, e));
		}
	}

	private boolean writeJobsOnSpreadSheet(String sheetId, List<Job> jobs, BasicServiceResponse response)
			throws IOException, GeneralSecurityException {
		List<List<Object>> values = new ArrayList<>();
		for (Job job : jobs) {
			List<Object> row = translateJobInRow(job);
			values.add(row);
		}
		boolean success = true;
		String range = configuration.getGoogleSheetTemplateJobSheetUpdatesRange();
		AppendValuesResponse result = wrapper.updateRange(sheetId, range, values);
		if (result.getUpdates().getUpdatedRows() != values.size()) {
			ObjectMapper mapper = new ObjectMapper();
			String jsonSRequest = mapper.writeValueAsString(jobs);
			response.setError(true);
			String errorDescription = "The number of appdates in the response mismatch the updates in the request";
			response.setErrorDescription(errorDescription);
			response.getWarnings().add(new ResponseErrorDetail("ERROR", errorDescription,
					"\n--REQUEST--\n" + jsonSRequest + "\n--RESPONSE--\n" + result.toPrettyString()));
			success = false;
		}
		return success;
	}

	private List<Object> translateJobInRow(Job job) {
		String[] jobHeader = configuration.getGoogleSheetTemplateJobSheetHeader().split(",");
		List<Object> row = new ArrayList<>();
		for (String column : jobHeader) {

			if ("CONTAINER".equals(column)) {
				row.add(job.getContainer());
			}
			if ("CODE".equals(column)) {
				row.add(job.getOriginalClient().getClientCode());
			}
			if ("CLIENT".equals(column)) {
				row.add(job.getJobClient());
			}
			if ("JOB ID".equals(column)) {
				row.add(job.getJobCode());
			}
			if ("ADDRESS ON DOCKET".equals(column)) {
				row.add(job.getOriginalDeliveryAddress());
			}
			if ("TOTAL BOXES".equals(column)) {
				row.add("" + job.getTotalBoxes());
			}
			if ("PANELS".equals(column)) {
				row.add("" + job.getTotalPanels());
			}
			if ("HARDWARE".equals(column)) {
				row.add("" + job.getTotalHardware());
			}
			if ("FRAMES".equals(column)) {
				row.add("" + job.getTotalFrames());
			}
			if ("SIZE".equals(column)) {
				row.add("" + job.getSize());
			}

			if ("DEALER".equals(column)) {
				row.add(configuration.getDealerFunction());
			}
			if ("SUMMARY".equals(column)) {
				row.add(configuration.getSummaryFunction());
			}
			if ("DELIVER TO".equals(column)) {
				row.add(configuration.getDeliverToFunction());
			}
		}
		return row;
	}

	@RequestMapping(path = "jobloader/sheet/{containerNumber}/update")
	@ResponseBody
	public SheetServiceResponse updateSheet(@PathVariable(name = "containerNumber") String containerNumber) {
		SheetServiceResponse response = new SheetServiceResponse("update sheet");
		Container container = containerRepository.findByContainerNumber(containerNumber);
		if (container == null) {
			response.setFound(0);
			response.getWarnings()
					.add(new ResponseErrorDetail("ERROR", "Could not find any container " + containerNumber, ""));
			return response;
		} else if (StringUtils.isBlank(container.getSheetId())) {
			response.setFound(0);
			response.getWarnings().add(new ResponseErrorDetail("ERROR",
					"Could not find any sheetId for container " + containerNumber, container.toString()));
			return response;
		}
		response.setFound(1);

		String sheetId = container.getSheetId();
		List<JobUpdate> rows = getJobUpdates(sheetId, response, container);
		if (rows == null) {
			return response;
		}
		for (JobUpdate update : rows) {
			Job job = jobRepository.findByJobCode(update.getJobId());
			if (job == null) {
				response.getWarnings().add(new ResponseErrorDetail("Warning",
						"There is a job on the google sheet that does not appear on the db\n", update.toString()));
				continue;
			}
			if (update.getDeliverTo() != null) {
				Client client = clientRepository.findByClientCode(update.getDeliverTo());
				if (client == null) {
					response.getWarnings()
							.add(new ResponseErrorDetail("Warning",
									"There is job on the google sheet with a  deliver to value not in the db\n",
									update.toString()));
				} else {
					job.setDeliverTo(client);

				}
				job.setDeliverToCode(update.getDeliverTo());
			}
			job.setDeliveryAddress(update.getDeliveryAddress());
			jobRepository.save(job);

		}

		return response;
	}

	private List<JobUpdate> getJobUpdates(String sheetId, BasicServiceResponse response, Container container) {
		ValueRange values;
		String range = configuration.getReadUpdateRange() + (container.getJobs().size() + 1);

		try {

			values = wrapper.getValueRange(sheetId, range);
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
			response.setError(true);
			response.getWarnings().add(new ResponseErrorDetail("ERROR",
					"Error getting the values from the sheetId " + sheetId + " and range " + range, "", e));
			return null;
		}
		List<JobUpdate> updates = new ArrayList<>();
		int ctr = 0;
		for (List<Object> row : values.getValues()) {
			try {
				JobUpdate job = new JobUpdate();
				job.setJobId(row.get(configuration.getJobIdColumnNumber()) != null
						? row.get(configuration.getJobIdColumnNumber()).toString()
						: null);
				job.setDeliverTo(row.get(configuration.getDeliverToColumnNumber()) != null
						? row.get(configuration.getDeliverToColumnNumber()).toString()
						: null);
//			job.setDeliveryAddress(row.get(configuration.getDeliveryAddressColumnNumber()) != null
//					? row.get(configuration.getDeliveryAddressColumnNumber()).toString()
//					: null);
//			job.setNotes(row.get(configuration.getNotesColumnNumber()) != null
//					? row.get(configuration.getNotesColumnNumber()).toString()
//					: null);
				updates.add(job);
			} catch (Exception e) {
				response.getWarnings().add(new ResponseErrorDetail("ERROR", "Error while parsing the row", row, e));
			}
			ctr++;
		}

		return updates;
	}

	private String duplicateFromClientReportTemplate(String containerNumber, CreateReportResponse response)
			throws IOException, GeneralSecurityException {

		List<Permission> permissions = getTemplateCopyPermissions();
		File result = wrapper.duplicateFromTemplate(configuration.getClientReportSheetId(),
				containerNumber + " Client Report", permissions);

		String sheetId = result.getId();
		String webContentLink = result.getWebViewLink();
		response.setSheetFullURL(webContentLink);
		response.setSheetId(sheetId);
		return sheetId;
	}

	private SheetServiceContainerData duplicateFromTemplate(String containerNumber, SheetServiceResponse response)
			throws IOException, GeneralSecurityException {

		List<Permission> permissions = getTemplateCopyPermissions();
		File result = wrapper.duplicateFromTemplate(configuration.getTemplateId(), containerNumber, permissions);

		String sheetId = result.getId();
		String webContentLink = result.getWebViewLink();

		SheetServiceContainerData sheetServiceContainerData = new SheetServiceContainerData(sheetId, webContentLink,
				containerNumber);
		response.getContainerResponse().add(sheetServiceContainerData);

		return sheetServiceContainerData;
	}

	private List<Permission> getTemplateCopyPermissions() {
		List<Permission> permissions = new ArrayList<>();
		permissions.add(createPermission("user", "owner", configuration.getTemplatePermissionOwner()));
		if (!configuration.getTemplatePermissionWriter().trim().equals("none")) {
			for (String email : configuration.getTemplatePermissionWriter().split(",")) {
				permissions.add(createPermission("user", "writer", email));
			}
		}
		if (!configuration.getTemplatePermissionReader().trim().equals("none")) {
			for (String email : configuration.getTemplatePermissionReader().split(",")) {
				permissions.add(createPermission("user", "reader", email));
			}
		}
		return permissions;
	}

	private List<Permission> getTemplateClientReportCopyPermissions() {
		List<Permission> permissions = new ArrayList<>();
		permissions.add(createPermission("user", "owner", configuration.getTemplateClientReportPermissionOwner()));
		if (!configuration.getTemplateClientReportPermissionWriter().trim().equals("none")) {
			for (String email : configuration.getTemplateClientReportPermissionWriter().split(",")) {
				permissions.add(createPermission("user", "writer", email));
			}
		}
		if (!configuration.getTemplateClientReportPermissionReader().trim().equals("none")) {
			for (String email : configuration.getTemplateClientReportPermissionReader().split(",")) {
				permissions.add(createPermission("user", "reader", email));
			}
		}
		return permissions;
	}

	private Permission createPermission(String type, String role, String email) {
		Permission p = new Permission();
		p.setType(type);
		p.setRole(role);
		p.setEmailAddress(email);
		return p;
	}

}
