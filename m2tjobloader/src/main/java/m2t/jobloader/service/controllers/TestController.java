package m2t.jobloader.service.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import m2t.jobloader.dao.model.Container;
import m2t.jobloader.dao.repositories.ContainerRepository;
import m2t.jobloader.service.controllers.model.BasicServiceResponse;
import m2t.jobloader.service.controllers.model.CreateReportResponse;
import m2t.jobloader.service.controllers.model.SheetServiceResponse;

@RestController
public class TestController {

	
	@Autowired
	JobLoaderService jobLoadService;
	@Autowired
	SheetController sheetController;
	@Autowired
	ContainerRepository containerRepository;
	
	
	@RequestMapping(path = "/test/{containerNumber}")
	public @ResponseBody Map<String, Object> test(@PathVariable("containerNumber")String containerNumber) throws GeneralSecurityException, IOException {
		Map<String, Object> response = jobLoadService.test();
		BasicServiceResponse create = sheetController.createSheet(containerNumber);
		response.put("create", create);
//		SheetServiceResponse update = sheetController.updateSheet(containerNumber);
//		response.put("update", update);
//		CreateReportResponse report = sheetController.createReport(containerNumber);
//		response.put("report", report);
//		try {
//			java.io.OutputStream os = java.nio.file.Files.newOutputStream(Paths.get("C:/Danilo/Andrew/"+((new Date()).getTime()) + ".pdf"));
//			Container container = containerRepository.findByContainerNumber(containerNumber);
//			
//			String sheetId = report.getSheetId();
//			sheetController.getWrapper().getDriveService().files().export(sheetId,"application/pdf").set("portrait", Boolean.TRUE).set("scale","4").executeAndDownloadTo(os);
//			os.flush();
//			os.close();
//			} catch (IOException | GeneralSecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return response;
		
		
	}
	
	@RequestMapping(path = "/test/print/{containerNumber}")
	public @ResponseBody CreateReportResponse print(@PathVariable("containerNumber")String containerNumber) throws GeneralSecurityException, IOException {
		SheetServiceResponse update = sheetController.updateSheet(containerNumber);
	
		CreateReportResponse report = sheetController.createReport(containerNumber);
	
		try {
			java.io.OutputStream os = java.nio.file.Files.newOutputStream(Paths.get("C:/Danilo/Andrew/"+((new Date()).getTime()) + ".pdf"));
			Container container = containerRepository.findByContainerNumber(containerNumber);
			
			String sheetId = report.getSheetId();
			sheetController.getWrapper().getDriveService().files().export(sheetId,"application/pdf").set("portrait", Boolean.TRUE).set("scale","4").set("printtitle", "true").executeAndDownloadTo(os);
			os.flush();
			os.close();
			} catch (IOException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return report;
		
		
	}
}
