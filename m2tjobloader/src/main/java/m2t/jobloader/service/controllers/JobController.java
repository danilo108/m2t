package m2t.jobloader.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.restart.ConditionalOnInitializedRestarter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.Container;
import m2t.jobloader.dao.model.Job;
import m2t.jobloader.dao.model.translators.JobTranslator;
import m2t.jobloader.dao.repositories.ClientRepository;
import m2t.jobloader.dao.repositories.ContainerRepository;
import m2t.jobloader.dao.repositories.JobRepository;
import m2t.jobloader.service.controllers.model.EntityServiceResponse;
import m2t.jobloader.service.controllers.model.ResponseErrorDetail;
import m2t.service.model.jobloader.JobDTO;

@RestController("/jobs")
public class JobController {
	@Autowired
	ContainerRepository containerRepository;
	@Autowired
	JobRepository jobRepository;
	@Autowired
	ClientRepository clientRepository;

	
	@RequestMapping(path="/{containerNumber}/add")
	@ResponseBody
	EntityServiceResponse<Job> addOrReplace(@PathVariable("containerNumber")String containerNumber, @RequestBody JobDTO jobDTO, @RequestParam(name="override", defaultValue="true")String overrideStr){
		EntityServiceResponse<Job> response = new EntityServiceResponse<>("addOrReplace", Job.class);
		boolean override = "TRUE".equals(overrideStr != null?overrideStr.toUpperCase():"TRUE");
		if(jobDTO == null) {
			response.setError(true);
			response.setErrorDescription("The DTO is null");
			return response;
		}
		Container container = containerRepository.findByContainerNumber(containerNumber);
		if(container == null) {
			response.setFound(0);
			response.setError(true);
			response.setErrorDescription("The container number " + containerNumber + " does not exist");
			return response;
		}
		if(jobDTO.getOriginalClientCode() == null) {
			response.setError(true);
			response.setErrorDescription("The Original client code is null and the job does not exist yet");
			return response;
		}
		Client originalClient = clientRepository.findByClientCode(jobDTO.getOriginalClientCode());
		if(originalClient == null) {
			response.setError(true);
			response.setErrorDescription("The Original client code code " + jobDTO.getOriginalClientCode() +" does not exist yet");
			return response;
		}
		Client deliverToClient = null;
		if(jobDTO.getJobDeliverTo() != null) {
			deliverToClient = clientRepository.findByClientCode(jobDTO.getJobDeliverTo());
		}
		Job job = jobRepository.findByJobCode(jobDTO.getJobNumber());
		Job upddatedJob = new JobTranslator().toDAO(jobDTO, originalClient, deliverToClient);
		upddatedJob.setContainer(containerNumber);
		if(job != null) {
			upddatedJob.setId(job.getId());
		}
		response.setReference(jobDTO.getJobNumber());
		
		try {
			
			if(override || job == null) {
				jobRepository.save(upddatedJob);
				response.setEntity(upddatedJob);
			}else {
				response.addWarning(new ResponseErrorDetail("warning", "The Job hasn't been updated because override = " + overrideStr + " and there was already a job with code " + job.getJobCode(), job));
				
			}
			response.setId(upddatedJob.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			response.setErrorDescription("Error while saving the job");
			response.addWarning(new ResponseErrorDetail("ERROR", "Error while saving the Job", job, e));
			return response;
		}
		return response;
	}
	

	@RequestMapping(path="/{jobNumber}/delete", method=RequestMethod.DELETE)
	@ResponseBody
	EntityServiceResponse<Job> deleteJob(@PathVariable("jobNumber")String jobNumber){
		EntityServiceResponse<Job> response = new EntityServiceResponse<>("addOrReplace", Job.class);
		
		Job job = jobRepository.findByJobCode(jobNumber);
		if(job == null) {
			response.setError(true);
			response.setFound(0);
			response.setErrorDescription("The job with code " + jobNumber + " does not exist");
			return response;
		}
		response.setId(job.getId().toString());
		response.setReference(jobNumber);
		try {
			jobRepository.delete(job);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(true);
			response.setErrorDescription("Error while deliting the job");
			response.addWarning(new ResponseErrorDetail("ERROR", "Error while saving the Job", job, e));
			return response;
		}
		return response;
	}
	
	
}
