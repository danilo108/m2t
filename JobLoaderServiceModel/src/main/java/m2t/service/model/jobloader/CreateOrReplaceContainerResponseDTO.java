package m2t.service.model.jobloader;

public class CreateOrReplaceContainerResponseDTO {

	private ContainerDTO deletedContainer;
	private String cointainerNumber;

	public ContainerDTO getDeletedContainer() {
		return deletedContainer;
	}

	public void setDeletedContainer(ContainerDTO containerDeleted) {
		this.deletedContainer = containerDeleted;
	}

	public String getCointainerNumber() {
		return cointainerNumber;
	}

	public void setCointainerNumber(String cointainerNumber) {
		this.cointainerNumber = cointainerNumber;
	}
	
	
	
	
}
