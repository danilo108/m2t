package m2t.jobloader.websitechecker.model;

import java.util.List;

public class WebContainerPage {

	List<WebContainerRecord> records;
	
	int maxPages;

	public int getMaxPages() {
		return maxPages;
	}

	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}

	public List<WebContainerRecord> getRecords() {
		return records;
	}

	public void setRecords(List<WebContainerRecord> records) {
		this.records = records;
	}
	
	
}
