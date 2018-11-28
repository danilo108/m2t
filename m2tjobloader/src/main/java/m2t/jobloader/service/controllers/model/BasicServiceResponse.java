package m2t.jobloader.service.controllers.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import m2t.jobloader.reports.factory.ReportFactoryResponse;

public class BasicServiceResponse {

	private int found;
	private boolean error;
	private String errorDescription;
	protected List<ResponseErrorDetail> warnings;
	private Map<String, BasicServiceResponse> operations;
	private String operationName;
	private String operationWithException;

	public BasicServiceResponse(String operationName) {
		super();
		this.operationName = operationName == null?"NA_"+(new Date().getTime()):operationName;
		this.operations = new HashMap<>();
		warnings = new ArrayList<>();
	}

	public BasicServiceResponse addOperationResponse( BasicServiceResponse response) {
		this.warnings.addAll(response.getWarnings().stream().map(warning ->{
			return new ResponseErrorDetail(warning.getErroCode(), warning.getErrorDescription(), warning.getDetails(), warning.getException());
		}).collect(Collectors.toList()));
		for(ResponseErrorDetail warnig:response.getWarnings()) {
			if(warnig.getException() != null) {
				this.operationWithException = response.getOperationName();
			}
		}
		if(operations == null) {
			operations = new HashMap<>();
		}
		BasicServiceResponse clone = clone(response);
		if(clone != null) {
			return this.operations.put(response.getOperationName(), clone);
		}else {
			return null;
		}
		
	}

	public BasicServiceResponse clone(BasicServiceResponse response) {
		BasicServiceResponse clone = null;
		try {
			clone = (BasicServiceResponse) response.clone();
		} catch (CloneNotSupportedException e) {
			String json = response.toString();
			ObjectMapper om = new ObjectMapper();
			try {
				clone = om.readValue(json, response.getClass());
			} catch (IOException e1) {
				
			}
					
		}
		return clone;
	}
	
	
	public String getOperationWithException() {
		return operationWithException;
	}

	public void setOperationWithException(String operationWithException) {
		this.operationWithException = operationWithException;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public List<ResponseErrorDetail> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<ResponseErrorDetail> warnings) {
		this.warnings = warnings;
	}

	public int getFound() {
		return found;
	}

	public void setFound(int found) {
		this.found = found;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	
	public void addWarning(ResponseErrorDetail warning ) {
		warnings.add(warning);
	}
	
	
	public Map<String, BasicServiceResponse> getOperations() {
		return operations;
	}
	public void setOperations(Map<String, BasicServiceResponse> operations) {
		this.operations = operations;
	}
	
	@Override
	public String toString() {
		try {
			ObjectMapper om = new ObjectMapper();
			return om.writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return super.toString();
		}
		
	}

}