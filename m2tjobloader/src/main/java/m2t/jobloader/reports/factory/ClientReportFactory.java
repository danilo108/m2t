package m2t.jobloader.reports.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.DimensionProperties;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.InsertDimensionRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.UpdateDimensionPropertiesRequest;

import m2t.service.model.jobloader.JobDTO;
import m2t.service.model.reports.ClientReportDTO;

@Component()
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientReportFactory extends BasicBatchRequestFactory {

	int startRow;

	public ClientReportFactory() {
		super();
		startRow = 0;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public List<Request> getCreateSheetsRequest(List<ClientReportDTO> reports){
		List<Request> list = new ArrayList<>();
		for(int index = 0; index < reports.size(); index++) {
			ClientReportDTO report = reports.get(index);
			String title = "" + index +" " + report.getClientName();
			if(index > 0) {
				
				list.addAll(getAddSheetRequests(title, index));
			}else {
				list.add(renameSheet(index, title));
			}
		}
		return list;
	}

	public List<Request> getRequestForDealer(ClientReportDTO data, int sheetNumber) {
		List<Request> list = new ArrayList<>();

		
		list.addAll(getDealerTitle(data, sheetNumber));
		this.startRow++;
		list.addAll(getDealerJobs(data, sheetNumber));
		list.addAll(getColumn1DealerSizeRequests(data, sheetNumber));

		return list;
	}

	public List<Request> getRequestForInstaller(ClientReportDTO data, int sheetNumber) {
		List<Request> list = new ArrayList<>();

		
		list.addAll(getInstallerTitle(data, sheetNumber));
		this.startRow++;
		list.addAll(getInstallerJobs(data, sheetNumber));
		list.addAll(getColumn1InstallerSizeRequests(data, sheetNumber));

		return list;
	}


	private List<Request> getColumn1DealerSizeRequests(ClientReportDTO data, int sheetNumber) {
		List<Request> requests = new ArrayList<>();
		requests.add(getSizeCellRequest( sheetNumber, true, 0, 1, configuration.getClientReportDealerColumn1Width()));
		requests.add(getSizeCellRequest( sheetNumber, true, 1, 2, configuration.getClientReportDealerColumn2Width()));
		requests.add(getSizeCellRequest( sheetNumber, true, 2, 3, configuration.getClientReportDealerColumn3Width()));
		requests.add(getSizeCellRequest( sheetNumber, false, 0, 1, configuration.getClientReportDealerTitleRowHeight()));
		requests.add(getSizeCellRequest( sheetNumber, false, 2, data.getJobs().size() + 2, configuration.getClientReportDealerJobsRowHeight()));
		return requests;
	}
	private List<Request> getColumn1InstallerSizeRequests(ClientReportDTO data, int sheetNumber) {
		List<Request> requests = new ArrayList<>();
		requests.add(getSizeCellRequest( sheetNumber, true, 0, 1, configuration.getClientReportInstallerColumn1Width()));
		requests.add(getSizeCellRequest( sheetNumber, true, 1, 2, configuration.getClientReportInstallerColumn2Width()));
		requests.add(getSizeCellRequest( sheetNumber, true, 2, 3, configuration.getClientReportInstallerColumn3Width()));
		requests.add(getSizeCellRequest( sheetNumber, true, 3, 4, configuration.getClientReportInstallerColumn4Width()));
		requests.add(getSizeCellRequest( sheetNumber, false, 0, 1, configuration.getClientReportDealerTitleRowHeight()));
		requests.add(getSizeCellRequest( sheetNumber, false, 2, data.getJobs().size() + 2, configuration.getClientReportDealerJobsRowHeight()));
		return requests;
	}

	private List<Request> getDealerJobs(ClientReportDTO data, int sheetNumber) {

		List<Request> requests = new ArrayList<>();
		int jobRows = 0;
		for (int i = 0; i < data.getJobs().size(); i++) {

			if (i % configuration.getClientReportDealerJobsPerRow().intValue() == 0 && i != 0) {
				// next printing page
				requests.addAll(getDealerTitle(data, sheetNumber));
				requests.add(getRowHeightRequest(sheetNumber, configuration.getClientReportDealerJobsRowHeight(),
						new Integer(this.startRow), new Integer(this.startRow + 1 + jobRows)));
				jobRows = 0;
				this.startRow++;

			}

			JobDTO job = data.getJobs().get(i);
			this.startRow++;
			requests.add(getJobNumberCell(job, sheetNumber, this.startRow));
//			requests.add(getJobCode(job, sheetNumber, startRow + i));	
			requests.add(getJobClientCell(job, sheetNumber, this.startRow, false));
			requests.add(getJobSummaryCell(job, sheetNumber, this.startRow, false));
			jobRows++;

		}
//		int numOfSpaceRows = configuration.getClientReportDealerJobsPerRow().intValue() - jobRows;
//		requests.add(getSpacer(sheetNumber, numOfSpaceRows));
		return requests;
	}
	
	private List<Request> getInstallerJobs(ClientReportDTO data, int sheetNumber) {

		List<Request> requests = new ArrayList<>();
		int jobRows = 0;
		for (int i = 0; i < data.getJobs().size(); i++) {


			JobDTO job = data.getJobs().get(i);
			this.startRow++;
			requests.add(getJobNumberCell(job, sheetNumber, this.startRow));
			requests.add(getJobCode(job, sheetNumber, startRow));	
			requests.add(getJobClientCell(job, sheetNumber, this.startRow, true));
			requests.add(getJobSummaryCell(job, sheetNumber, this.startRow, true));
			jobRows++;

		}
//		int numOfSpaceRows = configuration.getClientReportDealerJobsPerRow().intValue() - jobRows;
//		requests.add(getSpacer(sheetNumber, numOfSpaceRows));
		return requests;
	}

	private Request getSpacer(int sheetNumber, int numOfSpaceRows) {
		Request request = getRowHeightRequest(sheetNumber, configuration.getClientReportSpaceRowHeight(), this.startRow,
				this.startRow + numOfSpaceRows + 1);
		this.startRow += numOfSpaceRows;
		return request;

	}

	private Request getJobSummaryCell(JobDTO job, int sheetNumber, int startRow, boolean isInstaller) {
		String value = formatSummary(job.getTotalBoxes(), job.getTotalPanels(), job.getFormattedSize(), job.getTotalFrames(),
				job.getTotalHardware());
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = isInstaller ? 3 : 2;
		int columnEnd = columnStart + 1;
		String style = configuration.getClientReportStyleForDealerJobSummary();
		
		Request request = getCellValueAndStyleRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private Request getJobClientCell(JobDTO job, int sheetNumber, int startRow, boolean isInstaller) {
		String value = job.getJobClient().replaceAll(configuration.getClientReportJobClientRegExSearch(),
				configuration.getClientReportJobClientRegExReplaceWith());
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = isInstaller ? 2 : 1;
		int columnEnd = columnStart + 1;
		String style = configuration.getClientReportStyleForDealerJobClient();
		Request request = getCellValueAndStyleRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private Request getJobCode(JobDTO job, int sheetNumber, int startRow) {
		String value = job.getOriginalClientCode() + " " + job.getTotalBoxes();
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 1;
		int columnEnd = 2;
		String style = configuration.getClientReportStyleForDealerJobCode();
		Request request = getCellValueAndStyleRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private Request getJobNumberCell(JobDTO job, int sheetNumber, int startRow) {
		String value = job.getJobNumber();
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 0;
		int columnEnd = 1;
		String style = configuration.getClientReportStyleForDealerJobNumber();
		Request request = getCellValueAndStyleRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private List<Request> getDealerTitle(ClientReportDTO data, int sheetNumber) {
		List<Request> requests = new ArrayList<>();

		requests.add(getContainerCell(data.getContainerNumber(), sheetNumber, this.startRow));
		requests.add(getClientCodeCellDealer(data, sheetNumber, this.startRow));
		requests.add(getDealerTotalSummaryCell(data, sheetNumber, this.startRow));
		requests.add(getRowHeightRequestForTitle(sheetNumber, this.startRow));
		return requests;
	}
	private List<Request> getInstallerTitle(ClientReportDTO data, int sheetNumber) {
		List<Request> requests = new ArrayList<>();

		requests.add(getContainerCell(data.getContainerNumber(), sheetNumber, this.startRow));
		requests.add(getMergeRequest( sheetNumber, this.startRow, this.startRow, 1,3, "MERGE_COLUMNS"));
		requests.add(getClientCodeCellInstaller(data, sheetNumber, this.startRow));
		requests.add(getInstallerTotalSummaryCell(data, sheetNumber, this.startRow));
		requests.add(getRowHeightRequestForTitle(sheetNumber, this.startRow));
		return requests;
	}
	private Request getRowHeightRequestForTitle(int sheetNumber, int startRow) {

		Integer rowHeight = configuration.getClientReportDealerTitleRowHeight();
		Integer endIndex = new Integer(startRow + 1);
		Integer startIndex = new Integer(startRow);

		return getRowHeightRequest(sheetNumber, rowHeight, startIndex, endIndex);
	}

	private Request getRowHeightRequest(int sheetNumber, Integer rowHeight, Integer startIndex, Integer endIndex) {
		return new Request().setUpdateDimensionProperties(new UpdateDimensionPropertiesRequest()
				.setRange(new DimensionRange().setSheetId( sheetNumber).setDimension("ROWS").setStartIndex(startIndex)
						.setEndIndex(endIndex))
				.setFields("pixelSize").setProperties(new DimensionProperties().setPixelSize(rowHeight)));
	}
	private Request getInstallerTotalSummaryCell(ClientReportDTO data, int sheetNumber, int startRow) {
		String value = formatSummary(data.getTotalPanels(), "", data.getTotalFrames(), data.getTotalHardware());
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 3;
		int columnEnd = 4;
		String style = configuration.getClientReportStyleForDealerTotalSummary();
		Request request = getCellValueAndStyleRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private Request getDealerTotalSummaryCell(ClientReportDTO data, int sheetNumber, int startRow) {
		String value = formatSummary(data.getTotalPanels(), "", data.getTotalFrames(), data.getTotalHardware());
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 2;
		int columnEnd = 3;
		String style = configuration.getClientReportStyleForDealerTotalSummary();
		Request request = getCellValueAndStyleRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private String formatSummary(int totalPanels, String panelSize, int totalFrames, int totalHardware) {
		StringBuffer sb = new StringBuffer();
		if (totalPanels > 0) {
			sb.append("P:");
			sb.append(totalPanels);
			if (!StringUtils.isBlank(panelSize)) {
				sb.append(panelSize);
			}
		}
		if (totalFrames > 0) {
			sb.append(" F:");
			sb.append(totalFrames);
		}
		if (totalHardware > 0) {
			sb.append(" H:");
			sb.append(totalHardware);
		}
		return sb.toString();
	}
	
	private String formatSummary(int totalBoxes, int totalPanels, String panelSize, int totalFrames, int totalHardware) {
		StringBuffer sb = new StringBuffer();
		sb.append("T:");
		sb.append(totalBoxes);
		sb.append(" ");
		if (totalPanels > 0) {
			sb.append("P:");
			sb.append(totalPanels);
			if (!StringUtils.isBlank(panelSize)) {
				sb.append(panelSize);
			}
		}
		if (totalFrames > 0) {
			sb.append(" F:");
			sb.append(totalFrames);
		}
		if (totalHardware > 0) {
			sb.append(" H:");
			sb.append(totalHardware);
		}
		return sb.toString();
	}

	private Request getClientCodeCellDealer(ClientReportDTO data, int sheetNumber, int startRow) {
		String value = data.getClientName() + " " + data.getTotalBoxes();
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 1;
		int columnEnd = 2;
		String style = configuration.getClientReportStyleForDealerClientCode();
		Request request = getCellValueAndStyleRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}
	private Request getClientCodeCellInstaller(ClientReportDTO data, int sheetNumber, int startRow) {
		String value = data.getClientName() + " " + data.getTotalBoxes();
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 1;
		int columnEnd = 2;
		String style = configuration.getClientReportInstallerStyleClientCode();
		Request request = getCellValueAndStyleRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private Request getContainerCell(String containerNumber, int sheetNumber, int startRow) {
		String value = containerNumber;
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 0;
		int columnEnd = 1;
		String style = configuration.getClientReportStyleForContainer();
		Request request = getCellValueAndStyleRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	List<Request> getRequestForInstaller(ClientReportDTO data) {
		List<Request> list = new ArrayList<>();

		return list;
	}

}
