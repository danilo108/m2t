package m2t.service.model.jobloader;

import java.util.ArrayList;
import java.util.List;

public class JobDTO {
	List<BoxDTO> boxes;
	String jobNumber;
	String originalClientCode;
	String jobClient;
	String jobDeliverTo;
	String jobDeliveryAddress;
	String jobOriginalDeliveryAddress;
	
	float size;
	int totalBoxes;
	int totalPanels;
	int totalFrames;
	int totalHardware;
	
	

	public String getOriginalClientCode() {
		return originalClientCode;
	}


	public void setOriginalClientCode(String originalClientCode) {
		this.originalClientCode = originalClientCode;
	}


	public JobDTO() {
		boxes = new ArrayList<BoxDTO>();
	}

	
	public String getJobDeliveryAddress() {
		return jobDeliveryAddress;
	}


	public void setJobDeliveryAddress(String jobDeliveryAddress) {
		this.jobDeliveryAddress = jobDeliveryAddress;
	}


	public String getJobOriginalDeliveryAddress() {
		return jobOriginalDeliveryAddress;
	}


	public void setJobOriginalDeliveryAddress(String jobOriginalDeliveryAddress) {
		this.jobOriginalDeliveryAddress = jobOriginalDeliveryAddress;
	}


	public String getJobDeliverTo() {
		return jobDeliverTo;
	}


	public void setJobDeliverTo(String jobDeliverTo) {
		this.jobDeliverTo = jobDeliverTo;
	}


	public int getTotalBoxes() {
		return totalBoxes;
	}


	public void setTotalBoxes(int totalBoxes) {
		this.totalBoxes = totalBoxes;
	}


	public int getTotalPanels() {
		return totalPanels;
	}


	public void setTotalPanels(int totalPanels) {
		this.totalPanels = totalPanels;
	}


	public int getTotalFrames() {
		return totalFrames;
	}


	public void setTotalFrames(int totalFrames) {
		this.totalFrames = totalFrames;
	}


	public int getTotalHardware() {
		return totalHardware;
	}


	public void setTotalHardware(int totalHardware) {
		this.totalHardware = totalHardware;
	}


	public String getJobClient() {
		return jobClient;
	}


	public void setJobClient(String jobClient) {
		this.jobClient = jobClient;
	}


	public List<BoxDTO> getBoxes() {
		return boxes;
	}

	public void setBoxes(List<BoxDTO> boxes) {
		this.boxes = boxes;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	
}
