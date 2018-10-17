package m2t.jobloader.service.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.mortbay.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import m2t.jobloader.configuration.Configuration;
import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.Container;
import m2t.jobloader.dao.model.Job;
import m2t.jobloader.dao.repositories.ClientRepository;
import m2t.jobloader.dao.repositories.ContainerRepository;
import m2t.jobloader.dao.repositories.JobRepository;
import m2t.jobloader.service.controllers.model.JobUpdate;
import m2t.jobloader.service.controllers.model.ResponseErrorDetail;
import m2t.jobloader.service.controllers.model.SheetServiceResponse;
import m2t.jobloader.service.controllers.model.SheetServiceResponse.SheetServiceContainerData;

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

	@RequestMapping(path = "jobloader/sheet/create")
	@ResponseBody
	public SheetServiceResponse createSheet(@RequestBody(required = true) CreateSheetRequest request) {
		SheetServiceResponse response = new SheetServiceResponse();
		for (String containerNumber : request.getContainerNumbers()) {
			Container container = containerRepository.findByContainerNumber(containerNumber);
			if (container == null) {
				response.getWarnings().add(new ResponseErrorDetail("Warning",
						"The container " + containerNumber + " is not in the db", request.toString()));
				continue;
			}
			response.setFound(response.getFound() + 1);
			if (container.getSheetId() == null) {
				String sheetId;
				try {
					sheetId = duplicateFromTemplate(containerNumber, response);
				} catch (IOException | GeneralSecurityException e2) {
					response.setError(true);
					response.setErrorDescription("Error while duplicating the template ");
					response.getWarnings().add(new ResponseErrorDetail("Error", e2.getMessage(),
							"Container number :" + containerNumber, e2));
					return response;
				}

				container.setSheetId(sheetId);
				List<Job> jobs = jobRepository.findByContainerOrderByTotalBoxesDesc(containerNumber);
				try {
					writeJobsOnSpreadSheet(sheetId, jobs, response);
				} catch (IOException | GeneralSecurityException e) {
					response.setError(true);
					response.setErrorDescription("Error while updating the jobs in the template " + sheetId);
					ObjectMapper mapper = new ObjectMapper();
					String requestJson = "Job list parsing error";
					try {
						requestJson = mapper.writeValueAsString(jobs);
					} catch (JsonProcessingException e1) {
						response.getWarnings().add(new ResponseErrorDetail("Error",
								"Error while parsing the list of jobs in request", e.getMessage(), e1));
					}
					response.getWarnings().add(new ResponseErrorDetail("Error", e.getMessage(), requestJson, e));
				}

				containerRepository.save(container);
			}
		}

		return response;

	}

	private boolean writeJobsOnSpreadSheet(String sheetId, List<Job> jobs, SheetServiceResponse response)
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

		}
		return row;
	}

	@RequestMapping(path = "jobloader/sheet/{sheetId}update")
	@ResponseBody
	public SheetServiceResponse createSheet(@PathVariable(name = "sheetId") String sheetId) {
		SheetServiceResponse response = new SheetServiceResponse();
		List<JobUpdate> rows = getJobUpdates(sheetId);
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

			}
			job.setDeliveryAddress(update.getDeliveryAddress());
			jobRepository.save(job);

		}

		return null;
	}

	private List<JobUpdate> getJobUpdates(String sheetId) {
		ValueRange values = wrapper.getValueRange(sheetId, configuration.getGoogleSheetTemplateJobSheetUpdatesRange());
		List<JobUpdate> updates = new ArrayList<>();
		int ctr = 0;
		for (List<Object> row : values.getValues()) {
			JobUpdate job = new JobUpdate();
			job.setJobId(row.get(configuration.getJobIdColumnNumber()) != null
					? row.get(configuration.getJobIdColumnNumber()).toString()
					: null);
			job.setDeliverTo(row.get(configuration.getDeliverToColumnNumber()) != null
					? row.get(configuration.getDeliverToColumnNumber()).toString()
					: null);
			job.setDeliveryAddress(row.get(configuration.getDeliveryAddressColumnNumber()) != null
					? row.get(configuration.getDeliveryAddressColumnNumber()).toString()
					: null);
			job.setNotes(row.get(configuration.getNotesColumnNumber()) != null
					? row.get(configuration.getNotesColumnNumber()).toString()
					: null);
			updates.add(job);
			ctr++;
		}

		return updates;
	}

	private String duplicateFromTemplate(String containerNumber, SheetServiceResponse response)
			throws IOException, GeneralSecurityException {

		List<Permission> permissions = getTemplateCopyPermissions();
		File result = wrapper.duplicateFromTemplate(configuration.getTemplateId(), containerNumber, permissions);

		String sheetId = result.getId();
		String webContentLink = result.getWebViewLink();
		response.getContainerResponse().add(new SheetServiceContainerData(sheetId, webContentLink, containerNumber));

		return sheetId;
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

	private Permission createPermission(String type, String role, String email) {
		Permission p = new Permission();
		p.setType(type);
		p.setRole(role);
		p.setEmailAddress(email);
		return p;
	}

}
