package m2t.service.model.reports;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import m2t.service.model.jobloader.JobDTO;

public class ClientReportDTO {
	
	String clientName;
	String containerNumber;
	boolean installer;
	int totalBoxes;
	int totalPanels;
	int totalHardware;
	int totalFrames;
	float totalSize;
	String formattedSize;
	
	List<JobDTO> jobs;
	
	public ClientReportDTO() {
		jobs = new ArrayList<JobDTO>();
		totalSize = 0f;
	}
	
	
	public float getTotalSize() {
		return totalSize;
	}


	public void setTotalSize(float totalSize) {
		this.totalSize = totalSize;
	}


	public String getContainerNumber() {
		return containerNumber;
	}


	public void setContainerNumber(String containerNumber) {
		this.containerNumber = containerNumber;
	}


	public boolean isInstaller() {
		return installer;
	}
	public void setInstaller(boolean installer) {
		this.installer = installer;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
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
	public int getTotalHardware() {
		return totalHardware;
	}
	public void setTotalHardware(int totalHardware) {
		this.totalHardware = totalHardware;
	}
	public int getTotalFrames() {
		return totalFrames;
	}
	public void setTotalFrames(int totalFrames) {
		this.totalFrames = totalFrames;
	}
	public List<JobDTO> getJobs() {
		return jobs;
	}
	public void setJobs(List<JobDTO> jobs) {
		this.jobs = jobs;
	}
	
	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}
	
 
	public void addFrames(int frames) {
		this.totalBoxes += frames;
		this.totalFrames += frames;
	}
	
	public void addPanels(int panels) {
		this.totalBoxes += panels;
		this.totalPanels += panels;
	}
	
	public void addHardware(int hardware) {
		this.totalBoxes += hardware;
		this.totalHardware += hardware;
	}


	public String getFormattedSize() {
		return formattedSize;
	}


	public void setFormattedSize(String formattedSize) {
		this.formattedSize = formattedSize;
	}
	
	
}
