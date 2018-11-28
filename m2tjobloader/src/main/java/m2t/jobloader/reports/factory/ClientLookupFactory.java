package m2t.jobloader.reports.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.translators.ClientTranslator;
import m2t.jobloader.dao.model.translators.DTODAOTranslator;
import m2t.jobloader.service.controllers.ClientController;
import m2t.jobloader.service.controllers.model.BasicServiceResponse;
import m2t.service.model.jobloader.CustomerDTO;

@Component
public class ClientLookupFactory extends AbstractUpdateFilteredListRepot<Client, CustomerDTO> {

	
	
	@Value("${m2t.reports.clientlookup.style.generic")
	private String genericStyleForTitle;
	@Value("${m2t.reports.clientlookup.a1ValueRange")
	private String a1ValueRange;
	@Value("${m2t.reports.clientlookup.reportName")
	private String reportName;
	@Autowired
	private ClientController clientController;

	
	
	public ClientLookupFactory() {
		super();
	}
	
	


	@Override
	protected String getReportName() {
		return reportName;
	}


	@Override
	protected String getCellStyleForTitle(String key, int intValue) {

		return genericStyleForTitle;
	}

	@Override
	protected int getFirstRow() {
		return 0;
	}

	@Override
	protected int getSheetNumber() {
		return 0;
	}

	


	@Override
	protected String getCellStyleForProperty(String property, Map<String, String> rowMap, int rowNumber,
			int columnNumber) {
		return genericStyleForTitle;
	}




	@Override
	protected Class getTranslatorClass() {
		return ClientTranslator.class;
	}




	@Override
	protected String getA1ValuesRange() {
		return a1ValueRange;
	}




	@Override
	protected BasicServiceResponse deleteDTO(CustomerDTO dto) {
		return clientController.delete(dto.getCode());
	}




	@Override
	protected BasicServiceResponse addDTO(CustomerDTO dto) {
		return clientController.create(dto);
	}




	@Override
	protected BasicServiceResponse updateDTO(CustomerDTO dto) {
		return clientController.update(dto);
	}




	public String getGenericStyleForTitle() {
		return genericStyleForTitle;
	}




	public void setGenericStyleForTitle(String genericStyleForTitle) {
		this.genericStyleForTitle = genericStyleForTitle;
	}




	public String getA1ValueRange() {
		return a1ValueRange;
	}




	public void setA1ValueRange(String a1ValueRange) {
		this.a1ValueRange = a1ValueRange;
	}




	public void setReportName(String reportName) {
		this.reportName = reportName;
	}





	

	

}
