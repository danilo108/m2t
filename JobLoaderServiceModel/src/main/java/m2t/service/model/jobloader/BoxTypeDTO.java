package m2t.service.model.jobloader;

public enum BoxTypeDTO {
	PANEL("Panel"), FRAME("frame"), HARDWARE("hardware"), BLIND("Blind"), CHANNEL("t&b u channel"), LOUVER("louver");
	private String codeOnDocket;
	
	private BoxTypeDTO(String codeOnDocket) {
		this.codeOnDocket = codeOnDocket;
	}
	public String getCodeOnDocket() {
		return this.codeOnDocket;
	}
	
	public boolean equals(String value) {
		if(null == value) {
			return false;
		}
		return value.toUpperCase().trim().equals(getCodeOnDocket().toUpperCase());
	}
}
