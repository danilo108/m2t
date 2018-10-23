package m2t.jobloader.reports.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.Border;
import com.google.api.services.sheets.v4.model.Borders;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.DimensionProperties;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridProperties;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.InsertDimensionRequest;
import com.google.api.services.sheets.v4.model.NumberFormat;
import com.google.api.services.sheets.v4.model.Padding;
import com.google.api.services.sheets.v4.model.RepeatCellRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.TextFormat;
import com.google.api.services.sheets.v4.model.TextRotation;
import com.google.api.services.sheets.v4.model.UpdateDimensionPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;

import m2t.jobloader.configuration.Configuration;
import m2t.service.model.jobloader.JobDTO;
import m2t.service.model.reports.ClientReportDTO;

@Component()
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientReportFactory {

	@Autowired
	Configuration configuration;
	int startRow;

	public ClientReportFactory() {
		super();
		startRow = 0;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
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
			list.addAll(getAddSheetRequests("" + index +" " + report.getClientName(), index));
		}
		return list;
	}
	public List<Request> getRequestForDealer(ClientReportDTO data, int sheetNumber) {
		List<Request> list = new ArrayList<>();

		
		list.addAll(getDealerTitle(data, sheetNumber));
		this.startRow++;
		list.addAll(getDealerJobs(data, sheetNumber));
		list.addAll(getColumn1DealerSizeRequests(data, sheetNumber));

		// The spacer has to be a filler counting the number of rows remaining to
		// complete a page
		// You can calculate that from the title + jobs.size. Since you know how many
		// rows this report
		// takes you could also adjust the font size and row height for the job rows.

		// By the way, your requests should include the row height ...good luck!!
		// Make startRow a property of this class so if you need to print 2 pages for a
		// report you can increase startRow
		// and then you get it back for the next report
		return list;
	}

	

	private List<Request> getColumn1DealerSizeRequests(ClientReportDTO data, int sheetNumber) {
		List<Request> requests = new ArrayList<>();
		requests.add(getSizeCellRequest(100 + sheetNumber, true, 0, 1, configuration.getClientReportDealerColumn1Width()));
		requests.add(getSizeCellRequest(100 + sheetNumber, true, 1, 2, configuration.getClientReportDealerColumn2Width()));
		requests.add(getSizeCellRequest(100 + sheetNumber, true, 2, 3, configuration.getClientReportDealerColumn3Width()));
		requests.add(getSizeCellRequest(100 + sheetNumber, false, 0, 1, configuration.getClientReportDealerTitleRowHeight()));
		requests.add(getSizeCellRequest(100 + sheetNumber, false, 2, data.getJobs().size() + 2, configuration.getClientReportDealerJobsRowHeight()));
		return requests;
	}

	public Request getSizeCellRequest(int sheetNumber, boolean isDimensionColumn, int startIndex, int endIndex,
			int pixelSize) {
		return new Request().setUpdateDimensionProperties(
					new UpdateDimensionPropertiesRequest().setRange(
								new DimensionRange().setDimension(isDimensionColumn?"COLUMNS":"ROWS").setSheetId(sheetNumber).setStartIndex(new Integer(startIndex)).setEndIndex(new Integer(endIndex))
							).setProperties(
								new DimensionProperties().setPixelSize(new Integer(pixelSize))
							).setFields("pixelSize")
				);
	}

	private List<Request> getAddSheetRequests(String title, int sheetNumber) {
		List<Request> requests = new ArrayList<>();
		requests.add(
				new Request().setAddSheet(
					new AddSheetRequest().setProperties(
							new SheetProperties().setTitle(title).setIndex(sheetNumber).setGridProperties(
								new GridProperties().setRowCount(new Integer(1000)).setColumnCount(new Integer(20))
							).setSheetId(new Integer(100+sheetNumber))
					)
				)
		);
//		requests.add(
//				new Request().setInsertDimension(
//					new InsertDimensionRequest().setInheritFromBefore(Boolean.TRUE).setRange(
//							new DimensionRange().setSheetId(new Integer(sheetNumber)).setStartIndex(0).setEndIndex(1000).setDimension("ROWS")
//							)
//				)
//		);
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
		Request request = createRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
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
		Request request = createRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private Request getJobCode(JobDTO job, int sheetNumber, int startRow) {
		String value = job.getJobDeliverTo() + " " + job.getTotalBoxes();
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 1;
		int columnEnd = 2;
		String style = configuration.getClientReportStyleForDealerJobCode();
		Request request = createRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private Request getJobNumberCell(JobDTO job, int sheetNumber, int startRow) {
		String value = job.getJobNumber();
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 0;
		int columnEnd = 1;
		String style = configuration.getClientReportStyleForDealerJobNumber();
		Request request = createRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private List<Request> getDealerTitle(ClientReportDTO data, int sheetNumber) {
		List<Request> requests = new ArrayList<>();

		requests.add(getContainerCell(data.getContainerNumber(), sheetNumber, this.startRow));
		requests.add(getClientCodeCell(data, sheetNumber, this.startRow));
		requests.add(getTotalSummaryCell(data, sheetNumber, this.startRow));
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
				.setRange(new DimensionRange().setSheetId(100 + sheetNumber).setDimension("ROWS").setStartIndex(startIndex)
						.setEndIndex(endIndex))
				.setFields("pixelSize").setProperties(new DimensionProperties().setPixelSize(rowHeight)));
	}

	private Request getTotalSummaryCell(ClientReportDTO data, int sheetNumber, int startRow) {
		String value = formatSummary(data.getTotalPanels(), "", data.getTotalFrames(), data.getTotalHardware());
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 2;
		int columnEnd = 3;
		String style = configuration.getClientReportStyleForDealerTotalSummary();
		Request request = createRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
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

	private Request getClientCodeCell(ClientReportDTO data, int sheetNumber, int startRow) {
		String value = data.getClientName() + " " + data.getTotalBoxes();
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 1;
		int columnEnd = 2;
		String style = configuration.getClientReportStyleForDealerClientCode();
		Request request = createRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private Request getContainerCell(String containerNumber, int sheetNumber, int startRow) {
		String value = containerNumber;
		int rowStart = startRow;
		int rowEnd = startRow + 1;
		int columnStart = 0;
		int columnEnd = 1;
		String style = configuration.getClientReportStyleForContainer();
		Request request = createRequest(value, sheetNumber, rowStart, rowEnd, columnStart, columnEnd, style);
		return request;
	}

	private Request createRequest(String cellValue, int sheetNumber, int startRow, int endRow, int startColumn,
			int endColumn, String style) {

		return new Request().setRepeatCell(new RepeatCellRequest()
				.setCell(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(cellValue))
						.setUserEnteredFormat(getCellFormat(style)))
				.setRange(new GridRange().setSheetId(100 + sheetNumber).setStartRowIndex(startRow).setEndRowIndex(endRow)
						.setStartColumnIndex(startColumn).setEndColumnIndex(endColumn))
				.setFields("*"));
	}

	private CellFormat getCellFormat(String style) {
		CellFormat cellFormat = new CellFormat();
		cellFormat.setTextFormat(new TextFormat());
		cellFormat.setTextRotation(new TextRotation());

		Map<String, String> styleAttributes = parseStyle(style);
		styleAttributes.keySet().stream().forEach(attribute -> {
			String value = styleAttributes.get(attribute);
			if ("font-size".equals(attribute)) {
				cellFormat.getTextFormat().setFontSize(new Integer(value));
			} else if ("font-family".equals(attribute)) {
				cellFormat.getTextFormat().setFontFamily(value);
			} else if ("bold".equals(attribute)) {
				cellFormat.getTextFormat().setBold(new Boolean(value));
			} else if ("horizontal-alignment".equals(attribute)) {
				cellFormat.setHorizontalAlignment(value);
			} else if ("vertical-alignment".equals(attribute)) {
				cellFormat.setVerticalAlignment(value);
			} else if ("text-direction".equals(attribute)) {
				cellFormat.setTextDirection(value);
			} else if ("text-direction-angle".equals(attribute)) {
				cellFormat.getTextRotation().setAngle(new Integer(value));
			} else if ("text-direction-vertical".equals(attribute)) {
				cellFormat.getTextRotation().setVertical(new Boolean(value));
			} else if ("wrap-strategy".equals(attribute)) {
				cellFormat.setWrapStrategy(value);
			} else if ("backgroud-color".startsWith(StringUtils.substringBeforeLast(attribute, "."))) {
				if (cellFormat.getBackgroundColor() == null) {
					cellFormat.setBackgroundColor(new Color());
				}
				cellFormat.getBackgroundColor().set(StringUtils.substringAfterLast(attribute, "."), value);

			} else if ("borders".startsWith(StringUtils.substringBeforeLast(attribute, "."))) {
				if (cellFormat.getBorders() == null) {
					cellFormat.setBorders(new Borders());
				}
				if ("borders-bottom".startsWith(StringUtils.substringBeforeLast(attribute, "."))) {
					if (cellFormat.getBorders().getBottom() == null) {
						cellFormat.getBorders().setBottom(new Border());
					}
					cellFormat.getBorders().getBottom().set(StringUtils.substringAfterLast(attribute, "."), value);
				} else if ("borders-top".startsWith(StringUtils.substringBeforeLast(attribute, "."))) {
					if (cellFormat.getBorders().getTop() == null) {
						cellFormat.getBorders().setTop(new Border());
					}
					cellFormat.getBorders().getTop().set(StringUtils.substringAfterLast(attribute, "."), value);

				} else if ("borders-bottom".startsWith(StringUtils.substringBeforeLast(attribute, "."))) {
					if (cellFormat.getBorders().getLeft() == null) {
						cellFormat.getBorders().setLeft(new Border());
					}
					cellFormat.getBorders().getLeft().set(StringUtils.substringAfterLast(attribute, "."), value);
				} else if ("borders-bottom".startsWith(StringUtils.substringBeforeLast(attribute, "."))) {
					if (cellFormat.getBorders().getRight() == null) {
						cellFormat.getBorders().setRight(new Border());
					}
					cellFormat.getBorders().getRight().set(StringUtils.substringAfterLast(attribute, "."), value);
				}

			} else if ("number-format".startsWith(StringUtils.substringBeforeLast(attribute, "."))) {
				if (cellFormat.getNumberFormat() == null) {
					cellFormat.setNumberFormat(new NumberFormat());
				}
				cellFormat.getNumberFormat().set(StringUtils.substringAfterLast(attribute, "."), value);

			} else if ("padding".startsWith(StringUtils.substringBeforeLast(attribute, "."))) {
				if (cellFormat.getPadding() == null) {
					cellFormat.setPadding(new Padding());
				}
				if ("padding-bottom".equals(attribute)) {
					cellFormat.getPadding().setBottom(new Integer(value));
				} else if ("padding-top".equals(attribute)) {
					cellFormat.getPadding().setTop(new Integer(value));
				} else if ("padding-bottom".equals(attribute)) {
					cellFormat.getPadding().setLeft(new Integer(value));
				} else if ("padding-bottom".equals(attribute)) {
					cellFormat.getPadding().setRight(new Integer(value));
				}

			}

		});
		return cellFormat;
	}

	private Map<String, String> parseStyle(String style) {
		Map<String, String> map = new HashMap<>();
		String[] couples = style.split(";");
		for (String keyValue : couples) {
			String[] values = keyValue.split(":");
			if (values.length == 2) {
				map.put(values[0], values[1]);
			}
		}
		return map;
	}

	List<Request> getRequestForInstaller(ClientReportDTO data) {
		List<Request> list = new ArrayList<>();

		return list;
	}

}
