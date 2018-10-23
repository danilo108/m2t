package m2t.jobloader.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity(name="CLIENT")
public class Client {

	@Id
	@GeneratedValue
	Long id;
	@Column(unique=true, name="CODE")
	String clientCode;
	
	@Column(name="ADDRESS")
	String address;
	@Enumerated(EnumType.STRING)
	@Column(name="CLIENT_TYPE")
	ClientType clientType = ClientType.DEALER;
	
	@Enumerated(EnumType.STRING)
	@Column(name="DEFAULT_ZONE")
	DeliveryZone defaultZone;
	
	@Column(name="PHONE")
	String phone;
	
	@Column(name="ALARM")
	String alarm;
	
	@Column(name="GATE_CODE")
	String gateCode;
	
	@Column(name="LOCK")
	String lock;
	


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}

	public DeliveryZone getDefaultZone() {
		return defaultZone;
	}

	public void setDefaultZone(DeliveryZone defaultZone) {
		this.defaultZone = defaultZone;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public String getGateCode() {
		return gateCode;
	}

	public void setGateCode(String gateCode) {
		this.gateCode = gateCode;
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}
	
	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}
	
	
}
