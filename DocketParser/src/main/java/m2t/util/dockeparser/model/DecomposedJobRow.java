package m2t.util.dockeparser.model;

public class DecomposedJobRow {
	
	
	private int rowNumber;
	private String jobId;
	private String brokenClient;
	private String fullClientName;
	private int boxNumber;
	private int totalBoxes;
	private String boxType;
	
	
	public int getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getBrokenClient() {
		return brokenClient;
	}
	public void setBrokenClient(String brokenClient) {
		this.brokenClient = brokenClient;
	}
	public String getFullClientName() {
		return fullClientName;
	}
	public void setFullClientName(String fullClientName) {
		this.fullClientName = fullClientName;
	}
	public int getBoxNumber() {
		return boxNumber;
	}
	public void setBoxNumber(int boxNumber) {
		this.boxNumber = boxNumber;
	}
	public int getTotalBoxes() {
		return totalBoxes;
	}
	public void setTotalBoxes(int totalBoxes) {
		this.totalBoxes = totalBoxes;
	}
	public String getBoxType() {
		return boxType;
	}
	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(rowNumber);
		sb.append(" ");
		sb.append(jobId);
		sb.append(" ");
		sb.append(fullClientName);
		sb.append(" ");
		sb.append(boxType);
		sb.append(" ");
		sb.append(boxNumber);
		sb.append(" of ");
		sb.append(totalBoxes);
		return sb.toString();
	}
	

}
