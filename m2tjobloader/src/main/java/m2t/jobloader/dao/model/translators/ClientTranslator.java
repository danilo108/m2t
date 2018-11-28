package m2t.
jobloader.dao.model.translators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import m2t.jobloader.dao.model.Client;
import m2t.jobloader.dao.model.ClientType;
import m2t.jobloader.dao.model.DeliveryZone;
import m2t.service.model.jobloader.CustomerDTO;

@Component
public class ClientTranslator implements DTODAOTranslator<CustomerDTO, Client>{

	@Value("${m2t.model.translator.installerRegEX}")
	private String installerRegEx;
	private List<String> columns = Arrays.asList(	
			"clientType", "defaultZone", "alarm", "gateCode", "lock", "code", "address",
			"name", "phone" );
	
	public ClientTranslator() {
	}
	
	
	
	public String getInstallerRegEx() {
		return installerRegEx;
	}


	public void setInstallerRegEx(String installerRegEx) {
		this.installerRegEx = installerRegEx;
	}


	public synchronized Client toDAO(CustomerDTO dto) {
		Client dao = new Client();
		dao.setAddress( dto.getAddress() );
		dao.setClientCode( dto.getCode() );
		dao.setName(dto.getName() );
		dao.setPhone(dto.getPhone( ));
		
		                   
		dao.setClientType( ClientType.valueOf(dto.getClientType()));
		dao.setDefaultZone( DeliveryZone.valueOf(dto.getDefaultZone()));
		dao.setAlarm(dto.getAlarm());
		dao.setGateCode( dto.getGateCode());
		dao.setLock(dto.getLock());
		
		if(dto.getAddress().matches(installerRegEx)) {
		
		}
		if(dto.getAddress() != null) {
			dao.setClientType(dto.getAddress().matches(installerRegEx)?ClientType.INSTALLER:ClientType.DEALER);
		}else {
			dao.setClientType(ClientType.INSTALLER);
		}
		dao.setClientCode( dto.getCode());
		dao.setAddress( dto.getAddress());
		dao.setName( dto.getName());
		dao.setPhone( dto.getPhone());
		return dao;
		
	}
	
	public synchronized CustomerDTO toDTO(Client dao) {
		
		CustomerDTO dto = new CustomerDTO();
		dto.setAddress( dao.getAddress() );
		dto.setCode( dao.getClientCode() );
		dto.setName(dao.getName() );
		dto.setPhone(dao.getPhone( ));
		
		if(dao.getClientType() != null) {                  
			dto.setClientType( dao.getClientType().toString());
		}
		if(dao.getDefaultZone() != null) {
			dto.setDefaultZone(dao.getDefaultZone().toString());
		}
		dto.setAlarm(dao.getAlarm());
		dto.setGateCode( dao.getGateCode());
		dto.setLock(dao.getLock());
		dto.setAddress( dao.getAddress());
		dto.setName( dao.getName());
		dto.setPhone( dao.getPhone());
		return dto;
		
	}


	@Override
	public CustomerDTO toDTO(Map<String, String> values) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<String, String> toMap(CustomerDTO dto) {
		Map<String, String> map = new HashMap<>();
		map.put("code", dto.getClientType());
		map.put("name", dto.getClientType());
		map.put("address", dto.getAddress());
		map.put("defaultZone", dto.getDefaultZone());
		map.put("phone", dto.getPhone());
		map.put("clientType", dto.getClientType());
		map.put("gateCode", dto.getGateCode());
		map.put("lock", dto.getLock());
		map.put("alarm", dto.getAlarm());
		return map;
	}


	public List<String> getColumns() {
		return columns;
	}


	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	
}
