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
	@Value("${m2t.googlesheet.template.jobsheet.deliverToFunction}")
	private String deliverToFunction;
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
	
	
	@Value("${m2t.googlesheet.template.jobsheet.readUpdatesRange}")
	private String readUpdateRange;


	@Value("${m2t.googlesheet.template.clientreport.sheetId}")
	private String clientReportSheetId;
	
	@Value("${m2t.googlesheet.template.clientReport.permissions.owner}")
	private String templateClientReportPermissionOwner;
	
	@Value("${m2t.googlesheet.template.clientReport.permissions.writer}")
	private String templateClientReportPermissionWriter;
	
	@Value("${m2t.googlesheet.template.clientReport.permissions.reader}")
	private String templateClientReportPermissionReader;
	
	
	@Value("${m2t.googlesheet.template.clientReport.format.container}")
	private String clientReportStyleForContainer;
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.clientCode}")
	private String clientReportStyleForDealerClientCode;
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.totalSummary}")
	private String clientReportStyleForDealerTotalSummary;
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.jobNumber}")
	private String clientReportStyleForDealerJobNumber;
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.jobClient}")
	private String clientReportStyleForDealerJobClient;
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.jobSummary}")
	private String clientReportStyleForDealerJobSummary;
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.jobCode}")
	private String clientReportStyleForDealerJobCode;
	
	@Value("${m2t.googlesheet.template.clientReport.jobClientAdjustRegeExSearch}")
	private String clientReportJobClientRegExSearch;	
	
	@Value("${m2t.googlesheet.template.clientReport.jobClientAdjustRegeExReplaceWith}")
	private String clientReportJobClientRegExReplaceWith;
	

	@Value("${m2t.googlesheet.template.clientReport.format.dealer.title.rowheight}")
	private Integer clientReportDealerTitleRowHeight;
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.jobs.rowheight}")
	private Integer clientReportDealerJobsRowHeight;
	
	
	@Value("${m2t.googlesheet.template.clientReport.layout.dealer.jobsperpage}")
	private Integer clientReportDealerJobsPerRow;

	@Value("${m2t.googlesheet.template.clientReport.format.space.rowheight}")
	private Integer clientReportSpaceRowHeight;
	
	@Value("${m2t.google.api.timeout}")
	private int googleApiTimeout;
	
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.column1Width}")
	private int clientReportDealerColumn1Width;
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.column2Width}")
	private int clientReportDealerColumn2Width;
	
	@Value("${m2t.googlesheet.template.clientReport.format.dealer.column3Width}")
	private int clientReportDealerColumn3Width;
	
	@Value("${m2t.googlesheet.template.clientReport.format.installer.column1Width}")
	private int clientReportInstallerColumn1Width;
	
	@Value("${m2t.googlesheet.template.clientReport.format.installer.column2Width}")
	private int clientReportInstallerColumn2Width;
	
	@Value("${m2t.googlesheet.template.clientReport.format.installer.column3Width}")
	private int clientReportInstallerColumn3Width;
	
	
	@Value("${m2t.googlesheet.template.clientReport.format.installer.clientCode}")
	private String clientReportInstallerStyleClientCode;	
	
	
	public int getClientReportInstallerColumn1Width() {
		return clientReportInstallerColumn1Width;
	}
	public void setClientReportInstallerColumn1Width(int clientReportInstallerColumn1Width) {
		this.clientReportInstallerColumn1Width = clientReportInstallerColumn1Width;
	}
	public int getClientReportInstallerColumn2Width() {
		return clientReportInstallerColumn2Width;
	}
	public void setClientReportInstallerColumn2Width(int clientReportInstallerColumn2Width) {
		this.clientReportInstallerColumn2Width = clientReportInstallerColumn2Width;
	}
	public int getClientReportInstallerColumn3Width() {
		return clientReportInstallerColumn3Width;
	}
	public void setClientReportInstallerColumn3Width(int clientReportInstallerColumn3Width) {
		this.clientReportInstallerColumn3Width = clientReportInstallerColumn3Width;
	}
	public String getClientReportInstallerStyleClientCode() {
		return clientReportInstallerStyleClientCode;
	}
	public void setClientReportInstallerStyleClientCode(String clientReportInstallerStyleClientCode) {
		this.clientReportInstallerStyleClientCode = clientReportInstallerStyleClientCode;
	}
	public int getClientReportDealerColumn1Width() {
		return clientReportDealerColumn1Width;
	}
	public void setClientReportDealerColumn1Width(int clientReportDealerColumn1Width) {
		this.clientReportDealerColumn1Width = clientReportDealerColumn1Width;
	}
	public int getClientReportDealerColumn2Width() {
		return clientReportDealerColumn2Width;
	}
	public void setClientReportDealerColumn2Width(int clientReportDealerColumn2Width) {
		this.clientReportDealerColumn2Width = clientReportDealerColumn2Width;
	}
	public int getClientReportDealerColumn3Width() {
		return clientReportDealerColumn3Width;
	}
	public void setClientReportDealerColumn3Width(int clientReportDealerColumn3Width) {
		this.clientReportDealerColumn3Width = clientReportDealerColumn3Width;
	}
	public int getGoogleApiTimeout() {
		return googleApiTimeout;
	}
	public void setGoogleApiTimeout(int googleApiTimeout) {
		this.googleApiTimeout = googleApiTimeout;
	}
	public Integer getClientReportSpaceRowHeight() {
		return clientReportSpaceRowHeight;
	}
	public void setClientReportSpaceRowHeight(Integer clientReportSpaceRowHeight) {
		this.clientReportSpaceRowHeight = clientReportSpaceRowHeight;
	}
	public Integer getClientReportDealerJobsPerRow() {
		return clientReportDealerJobsPerRow;
	}
	public void setClientReportDealerJobsPerRow(Integer clientReportDealerJobsPerRow) {
		this.clientReportDealerJobsPerRow = clientReportDealerJobsPerRow;
	}
	public Integer getClientReportDealerJobsRowHeight() {
		return clientReportDealerJobsRowHeight;
	}
	public void setClientReportDealerJobsRowHeight(Integer clientReportDealerJobsRowHeight) {
		this.clientReportDealerJobsRowHeight = clientReportDealerJobsRowHeight;
	}
	public Integer getClientReportDealerTitleRowHeight() {
		return clientReportDealerTitleRowHeight;
	}
	public void setClientReportDealerTitleRowHeight(Integer clientReportDealerTitleRowHeight) {
		this.clientReportDealerTitleRowHeight = clientReportDealerTitleRowHeight;
	}
	public String getClientReportJobClientRegExSearch() {
		return clientReportJobClientRegExSearch;
	}
	public void setClientReportJobClientRegExSearch(String clientReportJobClientRegExSearch) {
		this.clientReportJobClientRegExSearch = clientReportJobClientRegExSearch;
	}
	public String getClientReportJobClientRegExReplaceWith() {
		return clientReportJobClientRegExReplaceWith;
	}
	public void setClientReportJobClientRegExReplaceWith(String clientReportJobClientRegExReplaceWith) {
		this.clientReportJobClientRegExReplaceWith = clientReportJobClientRegExReplaceWith;
	}
	public String getClientReportStyleForDealerJobCode() {
		return clientReportStyleForDealerJobCode;
	}
	public void setClientReportStyleForDealerJobCode(String clientReportStyleForDealerJobCode) {
		this.clientReportStyleForDealerJobCode = clientReportStyleForDealerJobCode;
	}
	public String getClientReportStyleForDealerTotalSummary() {
		return clientReportStyleForDealerTotalSummary;
	}
	public void setClientReportStyleForDealerTotalSummary(String clientReportStyleForDealerTotalSummary) {
		this.clientReportStyleForDealerTotalSummary = clientReportStyleForDealerTotalSummary;
	}
	public String getClientReportStyleForDealerJobNumber() {
		return clientReportStyleForDealerJobNumber;
	}
	public void setClientReportStyleForDealerJobNumber(String clientReportStyleForDealerJobNumber) {
		this.clientReportStyleForDealerJobNumber = clientReportStyleForDealerJobNumber;
	}
	public String getClientReportStyleForDealerJobClient() {
		return clientReportStyleForDealerJobClient;
	}
	public void setClientReportStyleForDealerJobClient(String clientReportStyleForDealerJobClient) {
		this.clientReportStyleForDealerJobClient = clientReportStyleForDealerJobClient;
	}
	public String getClientReportStyleForDealerJobSummary() {
		return clientReportStyleForDealerJobSummary;
	}
	public void setClientReportStyleForDealerJobSummary(String clientReportStyleForDealerJobSummary) {
		this.clientReportStyleForDealerJobSummary = clientReportStyleForDealerJobSummary;
	}
	public String getClientReportStyleForDealerClientCode() {
		return clientReportStyleForDealerClientCode;
	}
	public void setClientReportStyleForDealerClientCode(String clientReportStyleForDealerClientCode) {
		this.clientReportStyleForDealerClientCode = clientReportStyleForDealerClientCode;
	}
	public String getClientReportStyleForContainer() {
		return clientReportStyleForContainer;
	}
	public void setClientReportStyleForContainer(String clientReportStyleForContainer) {
		this.clientReportStyleForContainer = clientReportStyleForContainer;
	}
	public String getTemplateClientReportPermissionOwner() {
		return templateClientReportPermissionOwner;
	}
	public void setTemplateClientReportPermissionOwner(String templateClientReportPermissionOwner) {
		this.templateClientReportPermissionOwner = templateClientReportPermissionOwner;
	}
	public String getTemplateClientReportPermissionWriter() {
		return templateClientReportPermissionWriter;
	}
	public void setTemplateClientReportPermissionWriter(String templateClientReportPermissionWriter) {
		this.templateClientReportPermissionWriter = templateClientReportPermissionWriter;
	}
	public String getTemplateClientReportPermissionReader() {
		return templateClientReportPermissionReader;
	}
	public void setTemplateClientReportPermissionReader(String templateClientReportPermissionReader) {
		this.templateClientReportPermissionReader = templateClientReportPermissionReader;
	}
	public String getClientReportSheetId() {
		return clientReportSheetId;
	}
	public void setClientReportSheetId(String clientReportSheetId) {
		this.clientReportSheetId = clientReportSheetId;
	}
	public String getReadUpdateRange() {
		return readUpdateRange;
	}
	public void setReadUpdateRange(String readUpdateRange) {
		this.readUpdateRange = readUpdateRange;
	}
	public String getDeliverToFunction() {
		return deliverToFunction;
	}
	public void setDeliverToFunction(String deliverToFunction) {
		this.deliverToFunction = deliverToFunction;
	}
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
