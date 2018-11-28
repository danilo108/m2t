package m2t.jobloader.dao.repositories;

import org.springframework.data.repository.CrudRepository;

import m2t.jobloader.dao.model.Container;

public interface ContainerRepository extends CrudRepository<Container	, Long> {
	public Container findByContainerNumber(String containerNumber);
	public Container findByOrOriginalFileName(String originalFileName);
}
