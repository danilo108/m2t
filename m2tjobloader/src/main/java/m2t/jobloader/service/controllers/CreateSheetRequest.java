package m2t.jobloader.service.controllers;

import java.util.ArrayList;
import java.util.List;

public class CreateSheetRequest {
	
	private List<String> containerNumbers;
	
	public CreateSheetRequest() {
		containerNumbers = new ArrayList<>();
	}
	
	public List<String> getContainerNumbers() {
		return containerNumbers;
	}

	public void setContainerNumbers(List<String> containerNumbers) {
		this.containerNumbers = containerNumbers;
	}
	
	
	

}
