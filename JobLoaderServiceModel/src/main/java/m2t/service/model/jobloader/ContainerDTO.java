package m2t.service.model.jobloader;

import java.util.ArrayList;
import java.util.List;

public class ContainerDTO {
	
	String containerNumber;
	List<DocketDTO> dockets;
	String originalFileName;
	
	
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
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	
}
