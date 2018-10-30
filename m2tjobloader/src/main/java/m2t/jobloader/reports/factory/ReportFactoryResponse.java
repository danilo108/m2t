package m2t.jobloader.reports.factory;

import java.util.HashMap;
import java.util.Map;

import m2t.jobloader.service.controllers.model.BasicServiceResponse;

public class ReportFactoryResponse extends BasicServiceResponse {
	
	private Map<String, ReportFactoryResponse> operations;
	private String sheetId;
	private String fullURL;
	private Integer sheetNumber;
	private String operationName;
	
	public ReportFactoryResponse(String operationName) {
		super();
		operations = new HashMap<>();
		this.operationName = operationName;
	
	}
	
	public ReportFactoryResponse addOperationResponse( ReportFactoryResponse response) {
		this.warnings.addAll(response.getWarnings());
		return this.operations.put(response.getOperationName(), response);
		
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
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
	
	
	public Map<String, ReportFactoryResponse> getOperations() {
		return operations;
	}
	public void setOperations(Map<String, ReportFactoryResponse> operations) {
		this.operations = operations;
	}
	
	

}
