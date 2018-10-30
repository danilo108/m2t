package m2t.jobloader.service.controllers.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicServiceResponse {

	private int found;
	private boolean error;
	private String errorDescription;
	protected List<ResponseErrorDetail> warnings;

	public BasicServiceResponse() {
		super();
		warnings = new ArrayList<>();
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