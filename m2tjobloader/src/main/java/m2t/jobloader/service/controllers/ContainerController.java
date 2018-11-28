package m2t.jobloader.service.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.Container;
import m2t.jobloader.dao.model.Job;
import m2t.jobloader.dao.model.translators.ClientTranslator;
import m2t.jobloader.dao.model.translators.JobTranslator;
import m2t.jobloader.dao.repositories.ClientRepository;
import m2t.jobloader.dao.repositories.ContainerRepository;
import m2t.jobloader.service.controllers.model.EntityServiceResponse;
import m2t.jobloader.service.controllers.model.ResponseErrorDetail;
import m2t.service.model.jobloader.ContainerDTO;
import m2t.service.model.jobloader.CreateOrReplaceContainerResponseDTO;
import m2t.service.model.jobloader.CustomerDTO;
import m2t.service.model.jobloader.DocketDTO;
import m2t.service.model.jobloader.JobDTO;

@RestController("/container")
public class ContainerController {

	@Autowired
	ContainerRepository containerRepository;
	@Autowired
	ClientRepository clientRepository;
	@Autowired
	JobController jobController;
	
	@Autowired
	private ClientTranslator clientTranslator;
	
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public EntityServiceResponse<Container> createOrReplaceContainer(
			@RequestBody(required = true) ContainerDTO containerDTO, @RequestParam(defaultValue="true", name="overrideContainer")String overrideContainerStr, @RequestParam(defaultValue="true", name="overrideJobs")String overrideJobsrStr) {
		boolean overrideContainer = "TRUE".equals(overrideContainerStr != null?overrideContainerStr.toUpperCase():"TRUE");
		boolean overrideJobs = "TRUE".equals(overrideJobsrStr != null?overrideContainerStr.toUpperCase():"TRUE");
		
		EntityServiceResponse<Container> response = new EntityServiceResponse<>("create", Container.class);
		String containerNumber = containerDTO.getContainerNumber();
		Container container = containerRepository.findByContainerNumber(containerNumber);
		if(container != null && overrideContainer) {
			response.setFound(1);
			EntityServiceResponse<Container> deleteResponse = deleteContainer(containerNumber);
			response.addOperation("delete", deleteResponse);
			if(deleteResponse.isError()) {
				response.setError(true);
				response.addWarning(new ResponseErrorDetail("ERROR", "could not delete the container " + containerNumber + " to override it with a new one ", container.toString()));
				return response;
			}
		}
		
		container = new Container();
		container.setContainerNumber(containerNumber);
		try {
			containerRepository.save(container);
			response.setEntity(container);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			response.setErrorDescription("Error while saving the container");
			response.addWarning(new ResponseErrorDetail("error", "Error while saving the container", container, e));
		}
		List<Job> jobs = new ArrayList<>();
		
		for(int index = 0; index < containerDTO.getDockets().size(); index++) {

			DocketDTO docket = containerDTO.getDockets().get(index);
			Client client = getOrSaveClient(docket.getCustomer());
			final JobTranslator jt = new JobTranslator();
			
			if(!addJobs(docket, containerNumber, response, overrideJobs, jobs)) {
				return response;
			}
			
		}
		container.setJobs(jobs);
		try {
			containerRepository.save(container);
			response.setEntity(container);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			response.setErrorDescription("Error while saving the container");
			response.addWarning(new ResponseErrorDetail("error", "Error while saving the container", container, e));
		}

		return response;
		
	}
	
	
	@RequestMapping(path="{containerNumber}/delete", method=RequestMethod.DELETE)
	@ResponseBody
	public EntityServiceResponse<Container> deleteContainer(@PathVariable( name="containerNumber", required=true)String containerNumber){
		EntityServiceResponse<Container> response = new EntityServiceResponse<>("delete", Container.class);
		Container container = containerRepository.findByContainerNumber(containerNumber);
		if(container == null) {
			response.setFound(0);
			response.setError(true);
			response.setErrorDescription("The Container with number " + containerNumber + " does not exists");
		}else {
			response.setFound(1);
		}
		container.getJobs().stream().forEach(job ->{
			if(response.isError()) {
				return;
			}
			EntityServiceResponse<Job> deleteJobResponse = jobController.deleteJob(job.getJobCode());
			response.addOperation(deleteJobResponse);
			if(!response.isError() && deleteJobResponse.isError()) {
				response.setError(true);
				response.setErrorDescription("The first job that didn't get deleted was " + job.getJobCode());
			}
		});
		
		try {
			containerRepository.delete(container);
			response.setEntity(container);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			String errorDescription = "Error while deleting the container " + containerNumber;
			response.setErrorDescription(errorDescription);
			response.addWarning(new ResponseErrorDetail("error", errorDescription, container, e));
		}
		return response;
	}

	private boolean addJobs(DocketDTO docket, String containerNumber, EntityServiceResponse<Container> response, boolean override, List<Job> jobs) {
		if(jobs == null) {
			response.setErrorDescription("The function addJobs requires an initialised array of jobs");
			response.setError(true);
			return false;
		}
		for(JobDTO dto: docket.getJobs()) {
			EntityServiceResponse<Job> addJobREesponse = jobController.addOrReplace(containerNumber, dto, ""+ override);
			response.addOperation(addJobREesponse);
			if(!response.isError() && addJobREesponse.isError()) {
				response.setError(true);
				response.setErrorDescription("Error while saving Jobs from a docket delete the container before to try again");
				response.addWarning(new ResponseErrorDetail("error"	, "Error while saving Jobs from a docket for the container " + containerNumber, docket));
				return false;
			}else {
				jobs.add(addJobREesponse.getEntity());
			}
		}
		
		return true;
	}

	private Client getOrSaveClient(CustomerDTO customer) {
		Client client = clientRepository.findByClientCode(customer.getCode());
		if(client == null) {
			client=  clientTranslator.toDAO(customer);
			clientRepository.save(client);
		}
		return client;
	}
	
	

	
}
