package m2t.jobloader.service.controllers.model;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseErrorDetail{
	
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
	
	public ResponseErrorDetail(String erroCode, String errorDescription, List<Object> jsonList) {
		super();
		this.erroCode = erroCode;
		this.errorDescription = errorDescription;
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.details = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonList);
		} catch (JsonProcessingException e) {
			this.details = "Error parsing the list " + e.getMessage();
		}
	}
	public ResponseErrorDetail(String erroCode, String errorDescription, Object object) {
		super();
		this.erroCode = erroCode;
		this.errorDescription = errorDescription;
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.details = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			this.details = "Error parsing the list " + e.getMessage();
		}
	}
	public ResponseErrorDetail(String erroCode, String errorDescription, List<Object> jsonList,  Exception exception) {
		super();
		this.erroCode = erroCode;
		this.errorDescription = errorDescription;
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.details = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonList);
		} catch (JsonProcessingException e) {
			this.details = "Error parsing the list " + e.getMessage();
		}
		this.exception = exception;
	}
	public ResponseErrorDetail(String erroCode, String errorDescription, Object object,  Exception exception) {
		super();
		this.erroCode = erroCode;
		this.errorDescription = errorDescription;
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.details = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			this.details = "Error parsing the list " + e.getMessage();
		}
		this.exception = exception;
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
	
	
	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	public String toString() {
		try {
			ObjectMapper om = new ObjectMapper();
			return om.writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
		
	}

}
