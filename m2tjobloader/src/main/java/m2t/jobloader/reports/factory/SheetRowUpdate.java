package m2t.jobloader.reports.factory;

public class SheetRowUpdate<DTO> {
	public static enum OPERATIONS  {DELETE, ADD, UPDATE};
	
	private DTO dto;
	private OPERATIONS operation;
	
	public DTO getDto() {
		return dto;
	}
	public void setDto(DTO dto) {
		this.dto = dto;
	}
	public OPERATIONS getOperation() {
		return operation;
	}
	public void setOperation(OPERATIONS operation) {
		this.operation = operation;
	}
	
	
	

}
