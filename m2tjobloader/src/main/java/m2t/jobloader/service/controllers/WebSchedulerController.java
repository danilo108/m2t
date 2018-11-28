package m2t.jobloader.service.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import m2t.jobloader.dao.model.Container;
import m2t.jobloader.dao.model.translators.PDFToTEXTConverter;
import m2t.jobloader.dao.repositories.ContainerRepository;
import m2t.jobloader.notification.sns.M2TNotification;
import m2t.jobloader.notification.sns.NotificationFactory;
import m2t.jobloader.service.controllers.model.BasicServiceResponse;
import m2t.jobloader.service.controllers.model.ContainerSchedulerResponse;
import m2t.jobloader.service.controllers.model.CreateReportResponse;
import m2t.jobloader.service.controllers.model.PrintReportResponse;
import m2t.jobloader.service.controllers.model.ResponseErrorDetail;
import m2t.jobloader.service.controllers.model.SheetServiceResponse;
import m2t.jobloader.websitechecker.WebsiteChecker;
import m2t.jobloader.websitechecker.model.WebContainerPage;
import m2t.jobloader.websitechecker.model.WebContainerRecord;
import m2t.service.model.jobloader.ContainerDTO;
import m2t.service.model.jobloader.CreateOrReplaceContainerResponseDTO;
import m2t.util.dockeparser.controller.DocketParserController;

@Controller
public class WebSchedulerController {

	@Autowired
	ContainerRepository containerRepository;
	@Autowired
	WebsiteChecker webSiteChecker;
	@Autowired
	JobLoaderService jobLoadService;

	@Autowired
	SheetController sheetController;
	
	@Autowired
	NotificationFactory notificationFactory;
	
	@Value("${m2t.controllers.generateReport.url.prefix")
	String generateReportURL;
	
	@Value("${m2t.report.downloadReport.URL}")
	private String downloadReportURL;

	/*
	 * 
	 * I'm not going to do this. I will create the container and the sheet only when
	 * we have the jobs!!!! Web DB createCon addJobs CreateS b+ c J+ 0 0 0 case 1
	 * TRUE FALSE FALSE 0 0 1 impossible 0 1 0 impossible 1 0 0 error 1 TRUE TRUE
	 * TRUE 0 1 1 error 2 TRUE 1 0 1 impossible 1 1 0 case 2 FALSE TRUE TRUE 1 1 1
	 * case 3 FALSE FALSE FALSE
	 */

	@RequestMapping(path = "/scheduler/check")
	public @ResponseBody ContainerSchedulerResponse checkWebSite(@RequestParam(name="lastContainer", required=true)String lastContainer) {
		ContainerSchedulerResponse response = new ContainerSchedulerResponse("check");
		WebContainerPage page = null;
		try {
			if (!webSiteChecker.login()) {
				response.setError(true);
				response.setErrorDescription("Could not login into the website");
				return response;
			}
			page = webSiteChecker.getContainersPage();
			response.setWebContainerPage(page);
		} catch (Exception e) {
			e.printStackTrace();
			response.addWarning(new ResponseErrorDetail("ERROR", "Could not extract the container page", e));
			return response;
		}
		for(WebContainerRecord record: page.getRecords()){
			if(record.getNumber().endsWith(lastContainer)) {
				response.addWarning(new ResponseErrorDetail("INFO", "The system reached the last container in the parameter: " + lastContainer, record));
				break;
			}
			Container container = containerRepository.findByOrOriginalFileName(record.getNumber());
			boolean containerInDB = container != null;
			int boxesLoadedInt = 0;
			try {
				boxesLoadedInt = Integer.parseInt(record.getBoxesLoaded().trim());
			} catch (NumberFormatException e) {
				boxesLoadedInt = 0;
			}
			boolean boxesLoaded = boxesLoadedInt > 0;

			if (!containerInDB && boxesLoaded) {
				ContainerDTO containerDTO;
				try {
					PDFToTEXTConverter converter = new PDFToTEXTConverter();
					String convertedPDF = "";
					try {
						convertedPDF = converter.convertToText(webSiteChecker.getResponseInputStrem(record.getFullDownloadPDFURL()));
					} catch (Exception e) {
						e.printStackTrace();
						if(!response.isError()) {
							response.setError(true);
							String errorDescription = "Impossible to convert the pdf in text for container number: " + record.getNumber();
							response.setErrorDescription(errorDescription);
							response.addWarning(new ResponseErrorDetail("Error", errorDescription, record, e));
							continue;
						}
					}
					response.addWarning(new ResponseErrorDetail("INFO", "Docket converted for containerNumber:" + record.getNumber(), convertedPDF));
					DocketParserController parser = new DocketParserController(record.getNumber(),
							convertedPDF);
					containerDTO = parser.parseContainer();
				} catch (Exception e) {
					e.printStackTrace();
					response.setError(true);
					response.setErrorDescription("Error while downloading and parsing the pdf docket for the container "
							+ record.getNumber());
					response.addWarning(new ResponseErrorDetail("ERROR", "Erro while downloading and parsing the pdf docket for the container " + record.getNumber(), record, e));
					continue;
				}
				
				CreateOrReplaceContainerResponseDTO createContainerResponse;
				try {
					createContainerResponse = jobLoadService.createOrReplaceContainer(containerDTO);
							
//					response.addOperationResponse(createContainerResponse);
//					response.getWarnings().addAll(createContainerResponse.getWarnings());
				} catch (Exception e) {
					e.printStackTrace();
					if(!response.isError()) {
						response.setError(true);
						String errorDescription = "Error while creating the container : " + containerDTO.getContainerNumber();
						response.setErrorDescription(errorDescription);
						response.addWarning(new ResponseErrorDetail("error", errorDescription, containerDTO, e));
					}
				}
			
				
				if (containerRepository.findByOrOriginalFileName(record.getNumber()) == null) {
					response.setError(true);
					response.setErrorDescription("error while creating the container " + record.getNumber());

					continue;
				}
				try {
					BasicServiceResponse createSheetResponse = sheetController
							.createSheet(containerDTO.getContainerNumber());
				response.addOperationResponse(createSheetResponse);
//					response.getWarnings().addAll(createSheetResponse.getWarnings());
					if (createSheetResponse.isError() && !response.isError()) {
						response.setError(true);
						response.setErrorDescription(
								"error while creating the sheet for the container number " + record.getNumber());
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
					if(!response.isError()) {
						response.setError(true);
						String errorDescription = "Errro while creating the sheet for the container " + record.getNumber();
						response.setErrorDescription(errorDescription);
						response.addWarning(new ResponseErrorDetail("error", errorDescription, record, e));
					}
				}
				Container c = containerRepository.findByOrOriginalFileName(record.getNumber());
				record.setContainer(c);

				try {
					notificationFactory.createSheetCreatedForDeliveryManager(c.getContainerNumber(), c.getFullURL(), generateReportURL+c.getContainerNumber());
					notificationFactory.createSheetCreatedForInstallersManager(c.getContainerNumber(), c.getFullURL());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	

	@RequestMapping(path = "/report/print/{containerNumber}")
	public @ResponseBody PrintReportResponse printReport(@PathVariable("containerNumber")String containerNumber){
		PrintReportResponse response = new PrintReportResponse("print report");
		response.setContainerNumber(containerNumber);
		
		Container container;
		try {
			container = containerRepository.findByContainerNumber(containerNumber);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			String errorDescription = "Error while retrieving the container data from the DB";
			response.setErrorDescription(errorDescription);
			response.addWarning(new ResponseErrorDetail("ERROR", errorDescription, containerNumber, e));
			return response;
		}
		
		if(container == null) {
			response.setError(true);
			response.setErrorDescription("The container number : " + containerNumber + " does not exist");
			return response;
		}
		
		if(StringUtils.isBlank(container.getFullURL())) {
			response.addWarning(new ResponseErrorDetail("WARNING", "There was a request to create a report without a spread sheet", container));
			BasicServiceResponse createSheetResponse;
			try {
				createSheetResponse = sheetController.createSheet(containerNumber);
			} catch (Exception e) {
				e.printStackTrace();
				response.setError(true);
				String errorDescription = "Error while creating the spreadsheet... but the spreadsheet should be already there..whats going on?";
				response.setErrorDescription(errorDescription);
				response.addWarning(new ResponseErrorDetail("ERROR", errorDescription, containerNumber, e));
				return response;
			}
			response.addOperationResponse(createSheetResponse);
			if(createSheetResponse.isError()) {
				response.setError(true);
				response.setErrorDescription("Beside asking to create a report without having a spreadsheet where to update the jobs. There was an error creating the spreadsheet.");
				return response;
			}
		}
		response.setSpreadSheetFullURL(container.getFullURL());
		response.setSpreadSheetId(container.getSheetId());
		SheetServiceResponse update;
		try {
			update = sheetController.updateSheet(containerNumber);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			String errorDescription = "Error while retrieving the data from the spreadSheet";
			response.setErrorDescription(errorDescription);
			response.addWarning(new ResponseErrorDetail("ERROR", errorDescription, container, e));
			return response;
		}
		response.addOperationResponse(update);
		if(update.isError()) {
			response.setError(true);
			response.setErrorDescription("Error while retrieving the data from the spreadsheet " + container.getSheetId());
		}
		try {
			container = containerRepository.findByContainerNumber(containerNumber);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			String errorDescription = "Error while retrieving the container data from the DB after updating it from the spreadsheet";
			response.setErrorDescription(errorDescription);
			response.addWarning(new ResponseErrorDetail("ERROR", errorDescription, containerNumber, e));
			return response;
		}
		long unclompletedJobs = container.getJobs().stream().filter(job ->{
			return StringUtils.isBlank(job.getDeliverToCode());
		}).count();
		response.setUncompletedJobs(unclompletedJobs);
		
		
		CreateReportResponse report;
		try {
			report = sheetController.createReport(containerNumber);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			String errorDescription = "Error while creating the report spreadsheet";
			response.setErrorDescription(errorDescription);
			response.addWarning(new ResponseErrorDetail("ERROR", errorDescription, containerNumber, e));
			return response;
		}
		response.addOperationResponse(report);
		if(report.isError()) {
			response.setError(true);
			response.setErrorDescription("There was an error creating the report");
			return response;
		}
		M2TNotification notification = null;
		try {
			notification = notificationFactory.createFloorReportPrintNotification(response);
			notification.send();
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			response.setErrorDescription("BE AWARE!!!! The operation was SUCCESSFUL but you will not receive an email or sms this time. Use the following link to download the pdf, save this message and contact your customer support!!!  " + downloadReportURL + containerNumber);
			if(notification == null) {
				response.addWarning(new ResponseErrorDetail("ERROR", "Error sending the notification", "", e));
			}else {
				response.addWarning(new ResponseErrorDetail("ERROR", "Error sending the notification", notification, e));
			}
		}
		return response;
	}
	
	
	@GetMapping("/report/download/{containerNumber}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String containerNumber, HttpServletRequest request) throws Exception, GeneralSecurityException {
		Container container = containerRepository.findByContainerNumber(containerNumber);
		
      
        // Try to determine file's content type
        String contentType = null;
        contentType = "containerNumber";

//        // Fallback to the default content type if type could not be determined
//        if(contentType == null) {
//            contentType = "application/octet-stream";
//        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        sheetController.getWrapper().getDriveService().files().export(container.getReportSheetId(),"application/pdf").set("portrait", Boolean.TRUE).set("scale","4").set("printtitle", "true").executeAndDownloadTo(baos);
        baos.flush();
        baos.close();
        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
//
         return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Floor report - " + containerNumber + "\"").body(resource);
//      ;
    }
}
