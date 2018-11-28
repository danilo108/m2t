package m2t.jobloader.dao.model.translators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.ClientType;
import m2t.jobloader.dao.model.Job;
import m2t.jobloader.reports.factory.MasterReportFactoryConfiguration;
import m2t.service.model.jobloader.JobDTO;

@Component
public class JobTranslator implements DTODAOTranslator<JobDTO, Job> {
	@Value("${m2t.model.translator.installerRegEX}")
	private String installerRegEx;
	@Autowired
	private MasterReportFactoryConfiguration reportConfiguration;

	@Override
	public JobDTO toDTO(Job job) {
		if (job == null) {
			return null;
		}
		JobDTO dto = new JobDTO();
		dto.setContainer(job.getContainer());
		dto.setFormattedSize(job.getSize());
		if (job.getOriginalClient() != null) {
			dto.setOriginalClientCode(job.getOriginalClient().getClientCode());
			dto.setJobOriginalDeliveryAddress(job.getOriginalClient().getAddress());
		}
		dto.setJobDeliverTo(job.getDeliverToCode());
		dto.setJobDeliveryAddress(job.getDeliveryAddress());
		dto.setJobNumber(job.getJobCode());
		dto.setJobClient(job.getJobClient());

		dto.setTotalBoxes(job.getTotalBoxes());
		dto.setTotalFrames(job.getTotalFrames());
		dto.setTotalHardware(job.getTotalHardware());
		dto.setTotalPanels(job.getTotalPanels());

		return dto;

	}

	public Job toDAO(JobDTO dto, Client originalClient, Client deliverToClient) {
		Job dao = toDAO(dto);
		dao.setOriginalClient(originalClient);
		dao.setDeliverTo(deliverToClient);
		return dao;
	}

	public static String formatSize(float size) {
		if (size <= 0) {
			return ("??");
		} else if (size <= 0.8) {
			return ("-s");
		} else if (size <= 1.1) {
			return ("S");
		} else if (size <= 1.6) {
			return ("-m");
		} else if (size <= 1.8) {
			return ("M");
		} else if (size <= 2.2) {
			return ("L");
		} else if (size <= 2.6) {
			return ("VL");
		} else {
			return ("XL");
		}
	}

	@Override
	public Job toDAO(JobDTO dto) {
		Job dao = new Job();
		dao.setContainer(dto.getContainer());
		if ((dto.getSize() > 0 || (dto.getFormattedSize() != null && dto.getFormattedSize().equals("??") ) && dto.getTotalBoxes() > 0)
				|| dto.getFormattedSize() == null) {
			dao.setSize(formatSize(dto.getSize()/(dto.getTotalPanels()>0?dto.getTotalPanels():dto.getTotalBoxes())));
		} else if (dto.getFormattedSize() != null) {
			dao.setSize(dto.getFormattedSize());
		}
		dao.setDeliverToCode(dto.getJobDeliverTo());
		dao.setDeliveryAddress(dto.getJobDeliveryAddress());
		dao.setJobCode(dto.getJobNumber());
		dao.setJobClient(dto.getJobClient());
		dao.setSizeSQM(dto.getSize());
		dao.setTotalBoxes(dto.getTotalBoxes());
		dao.setTotalFrames(dto.getTotalFrames());
		dao.setTotalHardware(dto.getTotalHardware());
		dao.setTotalPanels(dto.getTotalPanels());
//		dao.setTotalBlinds(dto.getTotalBlinds());
		return dao;
	}

	@Override
	public JobDTO toDTO(Map<String, String> values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> toMap(JobDTO dto) {
		Map<String, String> map = new HashMap<>();
		map .put("container", dto.getContainer() );
		map.put("code", dto.getOriginalClientCode() );
		map.put("jobId", dto.getJobNumber() );
		map.put("clientOrignialAddress", dto.getJobOriginalDeliveryAddress() );
		map.put("totalBoxes", "" + dto.getTotalBoxes() );
//		map.put("totalBlinds", dto.getTotalBlinds());
		map.put("panels", "" + dto.getTotalPanels() );
		map.put("hardware", "" + dto.getTotalHardware() );
		map.put("frames", "" + dto.getTotalFrames() );
		map.put("size", dto.getFormattedSize() );
		if(dto.getJobOriginalDeliveryAddress() != null) {
			map.put("dealerInstaller", dto.getJobOriginalDeliveryAddress().matches(installerRegEx)?"INSTALLER":"DEALER" );
		}
		map.put("summary	", reportConfiguration.getJobSummaryFunction() );
		map.put("deliverTo", reportConfiguration.getDeliverToFunction() );
		map.put("deliveryAddress", dto.getJobDeliveryAddress() );
		return map;
	}

	@Override
	public List<String> getColumns() {
		return Arrays.asList("container", "code", "jobId", "clientOrignialAddress", "totalBoxes", "panels", "hardware",
				"frames", "size", "dealerInstaller", "summary	", "deliverTo", "deliveryAddress");
	}

}
