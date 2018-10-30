package m2t.jobloader.dao.model.translators;

import m2t.jobloader.dao.model.Job;
import m2t.service.model.jobloader.JobDTO;

public class JobTranslator {

	public JobDTO toDTO(Job job) {
		if(job == null) {
			return null;
		}
		JobDTO dto = new JobDTO();
		dto.setContainer (job.getContainer() );
		dto.setFormattedSize(job.getSize() );
		if(job.getOriginalClient() != null) {
			dto.setOriginalClientCode( job.getOriginalClient().getClientCode() );	
			dto.setJobOriginalDeliveryAddress( job.getOriginalClient().getAddress() );
		}
		dto.setJobDeliverTo(job.getDeliverToCode() );
		dto.setJobDeliveryAddress( job.getDeliveryAddress() );
		dto.setJobNumber( job.getJobCode() );
		dto.setJobClient(job.getJobClient());
		
		dto.setTotalBoxes( job.getTotalBoxes() );
		dto.setTotalFrames( job.getTotalFrames() );
		dto.setTotalHardware( job.getTotalHardware());
		dto.setTotalPanels(job.getTotalPanels());
		
		return dto;
		
	}
	
	

}
