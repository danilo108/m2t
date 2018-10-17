package m2t.jobloader.service.controllers.model;

import java.util.ArrayList;
import java.util.List;

public class SheetServiceResponse {
	
	public static class SheetServiceContainerData {
		public String sheetId;
		public String sheetFullURL;
		public String containerNumber;

		public SheetServiceContainerData() {
		}

		public SheetServiceContainerData(String sheetId, String sheetFullURL, String containerNumber) {
			super();
			this.sheetId = sheetId;
			this.sheetFullURL = sheetFullURL;
			this.containerNumber = containerNumber;
		}
		
	}

	private int found;
	private boolean error;
	private String errorDescription;
	private List<ResponseErrorDetail> warnings;
	
	
	


	private List<SheetServiceContainerData> containerResponse;




	public List<ResponseErrorDetail> getWarnings() {
		return warnings;
	}
	public void setWarnings(List<ResponseErrorDetail> warnings) {
		this.warnings = warnings;
	}
	public SheetServiceResponse() {
		warnings = new ArrayList<>();
		containerResponse = new ArrayList<>();
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
	public List<SheetServiceContainerData> getContainerResponse() {
		return containerResponse;
	}
	public void setContainerResponse(List<SheetServiceContainerData> containerResponse) {
		this.containerResponse = containerResponse;
	}
	
	
	

}
