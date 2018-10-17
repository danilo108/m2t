package m2t.service.model.jobloader;

import java.util.ArrayList;
import java.util.List;

public class DocketDTO {
	
	CustomerDTO customer;
	List<JobDTO> jobs;
	
	
	
	public DocketDTO() {
		jobs = new ArrayList<JobDTO>();
	}
	public CustomerDTO getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}
	public List<JobDTO> getJobs() {
		return jobs;
	}
	public void setJobs(List<JobDTO> jobs) {
		this.jobs = jobs;
	}
	
	

	
}
