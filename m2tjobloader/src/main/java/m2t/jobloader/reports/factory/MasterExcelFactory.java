package m2t.jobloader.reports.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SetBasicFilterRequest;

@Component
public class MasterExcelFactory extends BasicBatchRequestFactory {
	
	public List<Request> applyClavasFilters(){
		SetBasicFilterRequest filter = addFilter(1, 0, 10, 10, 10);
		applyCondition(filter, 0, "NOT_BLANK", null);
		List<Request> list = new ArrayList<>();
		list.add(new Request().setSetBasicFilter(filter));
		return list;
		
	}

}
