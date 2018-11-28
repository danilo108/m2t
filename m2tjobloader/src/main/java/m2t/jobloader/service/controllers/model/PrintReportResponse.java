package m2t.jobloader.service.controllers.model;

public class PrintReportResponse extends BasicServiceResponse{

	private long uncompletedJobs;
	private String reportSheetId;
	private String reportSheetFullURL;
	private String spreadSheetId;
	private String spreadSheetFullURL;
	private String containerNumber;
	
	public PrintReportResponse(String operationName) {
		super(operationName);
	}

	public long getUncompletedJobs() {
		return uncompletedJobs;
	}

	public void setUncompletedJobs(long uncompletedJobs) {
		this.uncompletedJobs = uncompletedJobs;
	}

	public String getReportSheetId() {
		return reportSheetId;
	}

	public void setReportSheetId(String reportSheetId) {
		this.reportSheetId = reportSheetId;
	}

	public String getReportSheetFullURL() {
		return reportSheetFullURL;
	}

	public void setReportSheetFullURL(String reportSheetFullURL) {
		this.reportSheetFullURL = reportSheetFullURL;
	}

	public String getContainerNumber() {
		return containerNumber;
	}

	public void setContainerNumber(String containerNumber) {
		this.containerNumber = containerNumber;
	}

	public String getSpreadSheetId() {
		return spreadSheetId;
	}

	public void setSpreadSheetId(String spreadSheetId) {
		this.spreadSheetId = spreadSheetId;
	}

	public String getSpreadSheetFullURL() {
		return spreadSheetFullURL;
	}

	public void setSpreadSheetFullURL(String spreadSheetFullURL) {
		this.spreadSheetFullURL = spreadSheetFullURL;
	}

	
	

}
