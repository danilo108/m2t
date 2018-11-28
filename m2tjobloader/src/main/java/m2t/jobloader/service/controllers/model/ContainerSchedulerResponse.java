package m2t.jobloader.service.controllers.model;

import m2t.jobloader.websitechecker.model.WebContainerPage;

public class ContainerSchedulerResponse extends BasicServiceResponse {

	
	private WebContainerPage webContainerPage;
	
	public ContainerSchedulerResponse(String operationName) {
		super(operationName);
	}

	public WebContainerPage getWebContainerPage() {
		return webContainerPage;
	}

	public void setWebContainerPage(WebContainerPage webContainerPage) {
		this.webContainerPage = webContainerPage;
	}
	
	
	

}
