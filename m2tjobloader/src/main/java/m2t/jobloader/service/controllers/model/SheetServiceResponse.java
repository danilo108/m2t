package m2t.jobloader.service.controllers.model;

import java.util.ArrayList;
import java.util.List;

import m2t.jobloader.reports.factory.ReportFactoryResponse;

public class SheetServiceResponse extends ReportFactoryResponse {
	
	
	private SheetServiceResponseData data = new SheetServiceResponseData();




	public SheetServiceResponse(String operationName) {
		super(operationName);
	
		data.containerResponse = new ArrayList<>();
	}
	public List<SheetServiceContainerData> getContainerResponse() {
		return data.containerResponse;
	}
	public void setContainerResponse(List<SheetServiceContainerData> containerResponse) {
		this.data.containerResponse = containerResponse;
	}
	
	
	

}
