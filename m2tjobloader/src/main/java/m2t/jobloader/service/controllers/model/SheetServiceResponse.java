package m2t.jobloader.service.controllers.model;

import java.util.ArrayList;
import java.util.List;

public class SheetServiceResponse extends BasicServiceResponse {
	
	
	private SheetServiceResponseData data = new SheetServiceResponseData();




	public SheetServiceResponse() {
		warnings = new ArrayList<>();
		data.containerResponse = new ArrayList<>();
	}
	public List<SheetServiceContainerData> getContainerResponse() {
		return data.containerResponse;
	}
	public void setContainerResponse(List<SheetServiceContainerData> containerResponse) {
		this.data.containerResponse = containerResponse;
	}
	
	
	

}
