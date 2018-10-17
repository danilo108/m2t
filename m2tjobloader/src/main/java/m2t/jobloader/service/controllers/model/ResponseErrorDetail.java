package m2t.jobloader.service.controllers.model;

public class ResponseErrorDetail {
	
	private String erroCode;
	private String errorDescription;
	private String details;
	private Exception exception;
	public ResponseErrorDetail(String erroCode, String errorDescription, String details) {
		super();
		this.erroCode = erroCode;
		this.errorDescription = errorDescription;
		this.details = details;
	}
	public ResponseErrorDetail(String erroCode, String errorDescription, String details, Exception exception) {
		super();
		this.erroCode = erroCode;
		this.errorDescription = errorDescription;
		this.details = details;
		this.exception = exception;
	}
	public String getErroCode() {
		return erroCode;
	}
	public void setErroCode(String erroCode) {
		this.erroCode = erroCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	

}
