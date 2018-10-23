package m2t.jobloader.service.controllers.model;

import java.util.ArrayList;
import java.util.List;

public class SheetServiceResponseData {
	public List<SheetServiceContainerData> containerResponse;

	public SheetServiceResponseData() {
		containerResponse = new ArrayList<SheetServiceContainerData>();
	}
}