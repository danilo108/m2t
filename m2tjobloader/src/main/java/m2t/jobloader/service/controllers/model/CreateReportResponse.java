package m2t.jobloader.service.controllers.model;

import java.util.ArrayList;
import java.util.List;

import m2t.service.model.reports.ClientReportDTO;

public class CreateReportResponse extends BasicServiceResponse {
	
	private String containerNumber;
	private String sheetId;
	private String sheetFullURL;
	List<String> clientNames;
	List<ClientReportDTO> clientReports;

	
	

	public CreateReportResponse() {
		super("create report");
		clientReports = new ArrayList<>();
		clientNames = new ArrayList<>();
	}

	public List<ClientReportDTO> getClientReports() {
		return clientReports;
	}

	public void setClientReports(List<ClientReportDTO> clientReports) {
		this.clientReports = clientReports;
	}

	public String getContainerNumber() {
		return containerNumber;
	}

	public void setContainerNumber(String containerNumber) {
		this.containerNumber = containerNumber;
	}

	public String getSheetId() {
		return sheetId;
	}

	public void setSheetId(String sheetId) {
		this.sheetId = sheetId;
	}

	public String getSheetFullURL() {
		return sheetFullURL;
	}

	public void setSheetFullURL(String sheetFullURL) {
		this.sheetFullURL = sheetFullURL;
	}

	public List<String> getClientNames() {
		return clientNames;
	}

	public void setClientNames(List<String> clientNames) {
		this.clientNames = clientNames;
	}
	
	
	

}
