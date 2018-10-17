package m2t.jobloader.dao.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import m2t.jobloader.dao.model.Job;

public interface JobRepository extends CrudRepository<Job	, Long> {
	
	List<Job> findByContainerOrderByTotalBoxesDesc(String container);
	Job findByJobCode(String jobCode);
	
	
}
