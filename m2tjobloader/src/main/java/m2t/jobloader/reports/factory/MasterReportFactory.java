package m2t.jobloader.reports.factory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import m2t.jobloader.dao.model.Job;
import m2t.jobloader.dao.model.translators.JobTranslator;
import m2t.jobloader.service.controllers.model.ResponseErrorDetail;
import m2t.service.model.jobloader.JobDTO;

@Component
public class MasterReportFactory extends AbstractFilteredListReport<Job,JobDTO> {

	@Autowired
	private MasterReportFactoryConfiguration reportConfiguration;
	


	public MasterReportFactory() {
		super();
		
	}

	@Override
	protected String getReportName() {
		return "Jobs2";
	}

	@Override
	protected String getCellStyleForTitle(String key, int intValue) {
		return reportConfiguration.getGenericTitleStyle();
	}

	@Override
	protected int getFirstRow() {
		return 1;
	}

	@Override
	protected int getSheetNumber() {
		return 0;
	}



	@Override
	protected Integer getColumnNumberForRowProperty(String propertyName) {
		// CONTAINER CODE JOB ID CLIENT ORIGNIAL ADDRESS TOTAL BOXES PANELS HARDWARE
		// FRAMES SIZE DEALER/INSTALLER SUMMARY DELIVER TO Delivery address
		return reportConfiguration.getFields().contains(propertyName)?reportConfiguration.getFields().indexOf(propertyName):null;
	}

	@Override
	protected Map<String, String> initialiseTitlePropertyMap() {
		Map<String, String> map = new HashMap<>();
		map .put("container", "Container" );
		map.put("code", "Code" );
		map.put("jobId", "Job Number" );
		map.put("clientOrignialAddress", "Original address" );
		map.put("totalBoxes", "Total boxes" );
		map.put("panels", "Panels" );
		map.put("hardware", "Hardware" );
		map.put("frames", "Frames" );
		map.put("size", "Size");
		map.put("dealerInstaller", "DEALER/INSTALLER" );
		map.put("summary	", "Summary" );
		map.put("deliverTo", "Deliver to" );
		map.put("deliveryAddress", "Delivery Address");
		return map;
	}

	@Override
	protected String getCellStyleForProperty(String property, Map<String, String> rowMap, int rowNumber,
			int columnNumber) {
		return reportConfiguration.getGenericCellStyle();
	}

	@Override
	protected Map<String, String> translateRowObjectToMap(JobDTO job) {
		Map<String, String> map = new HashMap<>();
		map .put("container", job.getContainer() );
		map.put("code", job.getOriginalClientCode() );
		map.put("jobId", job.getJobNumber() );
		map.put("clientOrignialAddress", job.getJobOriginalDeliveryAddress() );
		map.put("totalBoxes", "" + job.getTotalBoxes() );
		map.put("panels", "" + job.getTotalPanels() );
		map.put("hardware", "" + job.getTotalHardware() );
		map.put("frames", "" + job.getTotalFrames() );
		map.put("size", job.getFormattedSize() );
		map.put("dealerInstaller", isInstaller(job)?"INSTALLER":"DEALER" );
		map.put("summary	", reportConfiguration.getJobSummaryFunction() );
		map.put("deliverTo", reportConfiguration.getDeliverToFunction() );
		map.put("deliveryAddress", job.getJobDeliveryAddress() );
		return map;
	}

	

	private boolean isInstaller(JobDTO job) {
		String str = job.getJobOriginalDeliveryAddress();
		//3 Powderworks road North Narrabeen NSW 2101 AUSTRALIA
		return str.toUpperCase().contains("Powderworks") && str.toUpperCase().contains("Powderworks");
	}

	public MasterReportFactoryConfiguration getReportConfiguration() {
		return reportConfiguration;
	}

	public void setReportConfiguration(MasterReportFactoryConfiguration reportConfiguration) {
		this.reportConfiguration = reportConfiguration;
	}


	@Override
	protected ReportFactoryResponse executePreRequisitOperation() {
		ReportFactoryResponse response = new ReportFactoryResponse("create New sheet");
		try {
			File file = getGoogleWrapper().createNewSpreadSheet("Master Report", getReportFilePermissions());
			response.setSheetId(file.getId());
			response.setFullURL(file.getWebViewLink());
			setSheetId(file.getId());
			setSheetURL(file.getWebContentLink());
			response.setSheetNumber(new Integer(0));	
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
			response.setError(true);
			response.setErrorDescription("Error while creating a new spreadsheet for the report");
			response.getWarnings().add(new ResponseErrorDetail("ERROR", e.getMessage(), "", e));
		}finally {
			return response;
		}
	}

	private List<Permission> getReportFilePermissions() {
		List<Permission> permissions = new ArrayList<>();
		permissions.add(createPermission("user", "owner", configuration.getTemplatePermissionOwner()));
		if (!configuration.getTemplatePermissionWriter().trim().equals("none")) {
			for (String email : configuration.getTemplatePermissionWriter().split(",")) {
				permissions.add(createPermission("user", "writer", email));
			}
		}
		if (!configuration.getTemplatePermissionReader().trim().equals("none")) {
			for (String email : configuration.getTemplatePermissionReader().split(",")) {
				permissions.add(createPermission("user", "reader", email));
			}
		}
		return permissions;
	}
	private Permission createPermission(String type, String role, String email) {
		Permission p = new Permission();
		p.setType(type);
		p.setRole(role);
		p.setEmailAddress(email);
		return p;
	}

	@Override
	protected Class getTranslatorClass() {
		return JobTranslator.class;
	}

}
