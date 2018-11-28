package m2t.jobloader.service.controllers;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.comparator.Comparators;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import m2t.jobloader.reports.factory.ClientLookupFactory;
import m2t.jobloader.reports.factory.ClientReportFactory;
import m2t.jobloader.service.controllers.model.BasicServiceResponse;
import m2t.service.model.jobloader.CustomerDTO;

@RestController(value="/reports")
public class ReportsController {
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	ClientController clientController;
	
	@RequestMapping("/clients")
	@ResponseBody
	BasicServiceResponse createClientsLookupReport() {
		
		List<CustomerDTO> list = clientController.getAll();
		ClientLookupFactory clientLookupFactory = applicationContext.getBean(ClientLookupFactory.class); 
		return clientLookupFactory.buildReport(list, dto-> {return true;}, Comparator.comparing(CustomerDTO::getCode));
	}
	
	@RequestMapping("clients/{sheetId}/{sheetNumber}/update")
	@ResponseBody
	BasicServiceResponse updateClientsLookupReport(@PathVariable(name="sheetId", required=true)String sheetId, @PathVariable(name="sheetNumber", required=true)String sheetNumber) {
		ClientLookupFactory clientLookupFactory = applicationContext.getBean(ClientLookupFactory.class); 
		clientLookupFactory.setSheetId(sheetId);
		clientLookupFactory.setSheetNumber(new Integer(sheetNumber));
		return clientLookupFactory.update();
	}
	

}
