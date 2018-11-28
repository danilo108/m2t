package m2t.jobloader.reports.factory;

import java.util.HashMap;
import java.util.Map;

import m2t.jobloader.service.controllers.model.BasicServiceResponse;

public class ReportFactoryResponse extends BasicServiceResponse {
	
	private Map<String, ReportFactoryResponse> operations;
	private String sheetId;
	private String fullURL;
	private Integer sheetNumber;
	
	
	public ReportFactoryResponse(String operationName) {
		super(operationName);
		operations = new HashMap<>();
		
	
	}
	

	public Integer getSheetNumber() {
		return sheetNumber;
	}

	public void setSheetNumber(Integer sheetNumber) {
		this.sheetNumber = sheetNumber;
	}

	public String getSheetId() {
		return sheetId;
	}
	public void setSheetId(String sheetId) {
		this.sheetId = sheetId;
	}
	public String getFullURL() {
		return fullURL;
	}
	public void setFullURL(String fullURL) {
		this.fullURL = fullURL;
	}
	

	
	

}
