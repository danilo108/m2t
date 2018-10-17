package m2t.service.model.jobloader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetContainerResponseDTO {
	
	List<JobDTO> jobs;
	String containerNumber;
	Date arrivalTime;
	boolean found;
	

	
	public GetContainerResponseDTO() {
		jobs = new ArrayList<JobDTO>();
	}


	public List<JobDTO> getJobs() {
		return jobs;
	}


	public void setJobs(List<JobDTO> jobs) {
		this.jobs = jobs;
	}


	public String getContainerNumber() {
		return containerNumber;
	}


	public void setContainerNumber(String containerNumber) {
		this.containerNumber = containerNumber;
	}


	public Date getArrivalTime() {
		return arrivalTime;
	}


	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}


	public boolean isFound() {
		return found;
	}


	public void setFound(boolean found) {
		this.found = found;
	}
	
	
}
