package m2t.service.model.jobloader;

import java.util.ArrayList;
import java.util.List;

public class ContainerDTO {
	
	String containerNumber;
	List<DocketDTO> dockets;
	
	
	public ContainerDTO() {
		dockets = new ArrayList<DocketDTO>();
	}
	public String getContainerNumber() {
		return containerNumber;
	}
	public void setContainerNumber(String containerNumber) {
		this.containerNumber = containerNumber;
	}
	public List<DocketDTO> getDockets() {
		return dockets;
	}
	public void setDockets(List<DocketDTO> dockets) {
		this.dockets = dockets;
	}

	
}
