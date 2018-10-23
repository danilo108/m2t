package m2t.jobloader.dao.repositories;

import org.springframework.data.repository.CrudRepository;

import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.ClientType;

public interface ClientRepository extends CrudRepository<Client	, Long> {
	
	public Client findByClientCode(String clientCode);
	
	public Client findByClientCodeAndClientType(String clientCode, ClientType clientType);
}
