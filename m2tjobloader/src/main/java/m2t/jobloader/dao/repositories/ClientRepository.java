package m2t.jobloader.dao.repositories;

import org.springframework.data.repository.CrudRepository;

import m2t.jobloader.dao.model.Client;

public interface ClientRepository extends CrudRepository<Client	, Long> {
	
	public Client findByClientCode(String clientCode);
	

}
