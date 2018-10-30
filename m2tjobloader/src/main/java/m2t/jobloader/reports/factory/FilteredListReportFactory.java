package m2t.jobloader.reports.factory;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import com.google.api.services.sheets.v4.model.Request;

public interface FilteredListReportFactory<T> {

	public ReportFactoryResponse buildReport(List<T> list, Predicate<? super T> filterPredicate,  Comparator<? super T> sortComparator);

}
