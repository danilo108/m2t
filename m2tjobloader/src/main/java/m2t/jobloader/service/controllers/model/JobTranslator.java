package m2t.jobloader.service.controllers.model;

import m2t.jobloader.dao.model.Job;
import m2t.service.model.jobloader.JobDTO;

public class JobTranslator {
	
	public JobDTO translateToDTO(Job job) {
		JobDTO dto = new JobDTO();
		dto.setContainer( job.getContainer());
		dto.setJobDeliverTo( job.getDeliverToCode());
		dto.setJobClient(job.getJobClient());
		dto.setJobDeliveryAddress( job.getDeliveryAddress());
		dto.setJobNumber( job.getJobCode());
		dto.setOriginalClientCode( job.getOriginalClient().getClientCode());
		dto.setFormattedSize(job.getSize());
		dto.setTotalBoxes( job.getTotalBoxes());
		dto.setTotalFrames( job.getTotalFrames());
		dto.setTotalHardware( job.getTotalHardware());
		dto.setTotalPanels( job.getTotalPanels());
		return dto;
	}



}
