package m2t.jobloader.service.controllers.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityServiceResponse <T> extends BasicServiceResponse  {
	
	private String operationName;
	private String entityType;
	private String id;
	private String reference;
	private String JSONString;
	private T entity;
	

	
	public EntityServiceResponse(String operationName, Class<T> clazz) {
		super(operationName);
		this.entityType = clazz==null?this.getClass().getName(): clazz.getName();
	}
	
	public BasicServiceResponse addOperation(String operationName, BasicServiceResponse operation) {
		return getOperations().put(operationName, operation);
	}
	
	public BasicServiceResponse addOperation(EntityServiceResponse operation) {
		return getOperations().put(operation.getOperationName(), operation);
	}
	
	public String getOperationName() {
		
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJSONString() {
		return JSONString;
	}

	public void setJSONString(String jSONString) {
		JSONString = jSONString;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}
	
	
	
	
	
}
