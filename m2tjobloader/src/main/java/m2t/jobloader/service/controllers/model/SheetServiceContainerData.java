package m2t.jobloader.service.controllers.model;
public class SheetServiceContainerData {
		private String sheetId;
		private String sheetFullURL;
		private String containerNumber;

		public SheetServiceContainerData() {
		}

		public SheetServiceContainerData(String sheetId, String sheetFullURL, String containerNumber) {
			super();
			this.sheetId = sheetId;
			this.sheetFullURL = sheetFullURL;
			this.containerNumber = containerNumber;
		}

		public String getSheetId() {
			return sheetId;
		}

		public void setSheetId(String sheetId) {
			this.sheetId = sheetId;
		}

		public String getSheetFullURL() {
			return sheetFullURL;
		}

		public void setSheetFullURL(String sheetFullURL) {
			this.sheetFullURL = sheetFullURL;
		}

		public String getContainerNumber() {
			return containerNumber;
		}

		public void setContainerNumber(String containerNumber) {
			this.containerNumber = containerNumber;
		}
		
		
	}
