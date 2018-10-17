package m2t.util.dockeparser.model;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
	
	private int lineNumber;
	private String rowText;
	private boolean found;
	private List<String> rows;
	
	public SearchResult() {
		rows = new ArrayList<>();
		rowText = "";
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String getRowText() {
		return rowText;
	}
	public void setRowText(String rowText) {
		this.rowText = rowText;
	}
	public boolean isFound() {
		return found;
	}
	public void setFound(boolean found) {
		this.found = found;
	}

	public List<String> getRows() {
		return rows;
	}

	public void setRows(List<String> rows) {
		this.rows = rows;
	}
	
	public void addRow(String row) {
		if(row == null) {
			row = "";
		}
		row = row.replace("\\n", "");
		if(this.rowText == null) {
			this.rowText = row;
		}else {
			rowText +=row;
		}
		this.rows.add(row);
	}
	
	

}
