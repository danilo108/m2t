package m2t.jobloader.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

	@Value("${m2t.googlesheet.url.prefix}")
	private String googleSheetPrefix;

	@Value("${m2t.googlesheet.url.suffix}")
	private String googleSheetSufix;

	@Value("${m2t.googlesheet.template.sheetId}")
	private String templateId;
	
	@Value("${m2t.googlesheet.template.jobsheet.name}")
	private String googleSheetTemplateName;
	@Value("${m2t.googlesheet.url.suffix}")
	private String googleSheetsufix;
	@Value("${m2t.googlesheet.template.jobsheet.firstcolumn}")
	private String googleSheetTemplateFirstColumn;
	@Value("${m2t.googlesheet.template.jobsheet.firstrow}")
	private String googleSheetTemplateFirstRow;
	@Value("${m2t.googlesheet.template.jobsheet.header}")
	private String googleSheetTemplateJobSheetHeader;
	@Value("${m2t.googlesheet.template.jobsheet.dealerFunction}")
	private String dealerFunction;
	@Value("${m2t.googlesheet.template.jobsheet.summaryFunction}")
	private String summaryFunction;
	
	@Value("${m2t.googlesheet.template.jobsheet.updatesRange}")
	private String googleSheetTemplateJobSheetUpdatesRange;

	@Value("${m2t.googlesheet.template.jobsheet.jobIdColumnNumber}")
	private int jobIdColumnNumber;

	
	@Value("${m2t.googlesheet.template.jobsheet.deliverToColumnNumber}")
	private int deliverToColumnNumber;
	@Value("${m2t.googlesheet.template.jobsheet.deliveryAddressColumnNumber}")
	private int deliveryAddressColumnNumber;
	@Value("${m2t.googlesheet.template.jobsheet.notesColumnNumber}")
	private int notesColumnNumber;
	
	@Value("${m2t.google.api.applicationName}")
	private String applicationName;
	@Value("${m2t.google.api.credentialFilePath}")
	private String credentialFilePath;
	
	
	@Value("${m2t.googlesheet.template.permissions.owner}")
	private String templatePermissionOwner;
	
	@Value("${m2t.googlesheet.template.permissions.writer}")
	private String templatePermissionWriter;
	
	@Value("${m2t.googlesheet.template.permissions.reader}")
	private String templatePermissionReader;
	
	@Value("${m2t.test.DocketParser.uploadFolder}")
	private String testDocketsFolder;
	

	

	public String getTestDocketsFolder() {
		return testDocketsFolder;
	}
	public void setTestDocketsFolder(String testDocketsFolder) {
		this.testDocketsFolder = testDocketsFolder;
	}
	public String getDealerFunction() {
		return dealerFunction;
	}
	public void setDealerFunction(String dealerFunction) {
		this.dealerFunction = dealerFunction;
	}
	public String getSummaryFunction() {
		return summaryFunction;
	}
	public void setSummaryFunction(String summaryFunction) {
		this.summaryFunction = summaryFunction;
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
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getCredentialFilePath() {
		return credentialFilePath;
	}
	public void setCredentialFilePath(String credentialFilePath) {
		this.credentialFilePath = credentialFilePath;
	}
	public int getJobIdColumnNumber() {
		return jobIdColumnNumber;
	}
	public void setJobIdColumnNumber(int jobIdColumnNumber) {
		this.jobIdColumnNumber = jobIdColumnNumber;
	}
	public int getDeliverToColumnNumber() {
		return deliverToColumnNumber;
	}
	public void setDeliverToColumnNumber(int deliverToColumnNumber) {
		this.deliverToColumnNumber = deliverToColumnNumber;
	}
	public int getDeliveryAddressColumnNumber() {
		return deliveryAddressColumnNumber;
	}
	public void setDeliveryAddressColumnNumber(int deliveryAddressColumnNumber) {
		this.deliveryAddressColumnNumber = deliveryAddressColumnNumber;
	}
	public int getNotesColumnNumber() {
		return notesColumnNumber;
	}
	public void setNotesColumnNumber(int notesColumnNumber) {
		this.notesColumnNumber = notesColumnNumber;
	}
	public String getGoogleSheetTemplateJobSheetUpdatesRange() {
		return googleSheetTemplateJobSheetUpdatesRange;
	}
	public void setGoogleSheetTemplateJobSheetUpdatesRange(String googleSheetTemplateJobSheetUpdatesRange) {
		this.googleSheetTemplateJobSheetUpdatesRange = googleSheetTemplateJobSheetUpdatesRange;
	}
	public String getGoogleSheetPrefix() {
		return googleSheetPrefix;
	}
	public void setGoogleSheetPrefix(String googleSheetPrefix) {
		this.googleSheetPrefix = googleSheetPrefix;
	}
	public String getGoogleSheetSufix() {
		return googleSheetSufix;
	}
	public void setGoogleSheetSufix(String googleSheetSufix) {
		this.googleSheetSufix = googleSheetSufix;
	}
	public String getGoogleSheetsufix() {
		return googleSheetsufix;
	}
	public void setGoogleSheetsufix(String googleSheetsufix) {
		this.googleSheetsufix = googleSheetsufix;
	}
	public String getGoogleSheetTemplateFirstColumn() {
		return googleSheetTemplateFirstColumn;
	}
	public void setGoogleSheetTemplateFirstColumn(String googleSheetTemplateFirstColumn) {
		this.googleSheetTemplateFirstColumn = googleSheetTemplateFirstColumn;
	}
	public String getGoogleSheetTemplateFirstRow() {
		return googleSheetTemplateFirstRow;
	}
	public void setGoogleSheetTemplateFirstRow(String googleSheetTemplateFirstRow) {
		this.googleSheetTemplateFirstRow = googleSheetTemplateFirstRow;
	}
	public String getGoogleSheetTemplateJobSheetHeader() {
		return googleSheetTemplateJobSheetHeader;
	}
	public void setGoogleSheetTemplateJobSheetHeader(String googleSheetTemplateJobSheetHeader) {
		this.googleSheetTemplateJobSheetHeader = googleSheetTemplateJobSheetHeader;
	}
	public String getGoogleSheetTemplateName() {
		return googleSheetTemplateName;
	}
	public void setGoogleSheetTemplateName(String googleSheetTemplateName) {
		this.googleSheetTemplateName = googleSheetTemplateName;
	}
	
	
}
