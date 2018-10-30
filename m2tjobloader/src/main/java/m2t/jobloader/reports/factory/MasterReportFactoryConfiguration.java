package m2t.jobloader.reports.factory;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MasterReportFactoryConfiguration {
	@Value("${m2t.reports.master.style.genericTitle}")
	private String genericTitleStyle;
	@Value("${m2t.reports.master.style.genericCell}")
	private String genericCellStyle;
	@Value("${m2t.reports.master.fields}")
	private String fieldsString;
	private List<String> fields;
	@Value("${m2t.googlesheet.template.jobsheet.deliverToFunction}")
	private String deliverToFunction;
	@Value("${m2t.googlesheet.template.jobsheet.summaryFunction}")
	private String jobSummaryFunction;
	@Value("${m2t.googlesheet.template.permissions.owner}")
	private String templatePermissionOwner;
	
	@Value("${m2t.googlesheet.template.permissions.writer}")
	private String templatePermissionWriter;
	
	@Value("${m2t.googlesheet.template.permissions.reader}")
	private String templatePermissionReader;
	
	public MasterReportFactoryConfiguration() {
		
	}

	public String getGenericTitleStyle() {
		return genericTitleStyle;
	}

	public void setGenericTitleStyle(String genericTitleStyle) {
		this.genericTitleStyle = genericTitleStyle;
	}

	public String getGenericCellStyle() {
		return genericCellStyle;
	}

	public void setGenericCellStyle(String genericCellStyle) {
		this.genericCellStyle = genericCellStyle;
	}

	public String getFieldsString() {
		return fieldsString;
	}

	public void setFieldsString(String fieldsString) {
		this.fieldsString = fieldsString;
		this.fields = Arrays.asList(fieldsString.split(","));
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getDeliverToFunction() {
		return deliverToFunction;
	}

	public void setDeliverToFunction(String deliverToFunction) {
		this.deliverToFunction = deliverToFunction;
	}

	public String getJobSummaryFunction() {
		return jobSummaryFunction;
	}

	public void setJobSummaryFunction(String jobSummaryFunction) {
		this.jobSummaryFunction = jobSummaryFunction;
	}

	public String getTemplatePermissionOwner() {
		return templatePermissionOwner;
	}

	public void setTemplatePermissionOwner(String templatePermissionOwner) {
		this.templatePermissionOwner = templatePermissionOwner;
	}

	public String getTemplatePermissionWriter() {
		return templatePermissionWriter;
	}

	public void setTemplatePermissionWriter(String templatePermissionWriter) {
		this.templatePermissionWriter = templatePermissionWriter;
	}

	public String getTemplatePermissionReader() {
		return templatePermissionReader;
	}

	public void setTemplatePermissionReader(String templatePermissionReader) {
		this.templatePermissionReader = templatePermissionReader;
	}
	
	
}