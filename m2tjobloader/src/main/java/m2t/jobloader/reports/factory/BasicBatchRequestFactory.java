package m2t.jobloader.reports.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BasicFilter;
import com.google.api.services.sheets.v4.model.BooleanCondition;
import com.google.api.services.sheets.v4.model.Border;
import com.google.api.services.sheets.v4.model.Borders;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.ConditionValue;
import com.google.api.services.sheets.v4.model.DimensionProperties;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.FilterCriteria;
import com.google.api.services.sheets.v4.model.GridProperties;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.MergeCellsRequest;
import com.google.api.services.sheets.v4.model.NumberFormat;
import com.google.api.services.sheets.v4.model.Padding;
import com.google.api.services.sheets.v4.model.RepeatCellRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SetBasicFilterRequest;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.TextFormat;
import com.google.api.services.sheets.v4.model.TextRotation;
import com.google.api.services.sheets.v4.model.UpdateDimensionPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;

import m2t.jobloader.configuration.Configuration;

public class BasicBatchRequestFactory {
	
	@Autowired
	protected Configuration configuration;

	protected SetBasicFilterRequest addFilter( int sheetNumber, int startColumn, int endColumn, int startRow, int endRow ){
		GridRange gridRange = new GridRange().setStartColumnIndex(startColumn).setSheetId(sheetNumber);
		if(endColumn >=0) {
			gridRange.setEndColumnIndex(endColumn);
		}
		if(startRow >=0 ) {
			gridRange.setStartRowIndex(startRow);
		}
		if(endRow >=0) {
			gridRange.setEndRowIndex(endRow);
		}
		
		return new SetBasicFilterRequest().setFilter(
				new BasicFilter().setRange(
						gridRange
						)
				);
			
		
	}
	
	protected void applyCondition( SetBasicFilterRequest filter, int columnNumber, String conditionType, List<String> userEnteredValues ){
		List<ConditionValue> values = null;

		if(userEnteredValues != null && userEnteredValues.size() > 0) {
			values = new ArrayList<>();
			for(String v:userEnteredValues) {
				values.add(new ConditionValue().setUserEnteredValue(v));
			}
		}
		if(filter != null && filter.getFilter() != null ) {
		if(filter.getFilter().getCriteria() == null) {
			filter.getFilter().setCriteria(new HashMap<>());
		}
			filter.getFilter().getCriteria().put(""+columnNumber, 
				new FilterCriteria().setCondition(
						new BooleanCondition().setType(conditionType).setValues(values )
						)
				);
		}
	}

	protected Configuration getConfiguration() {
		return configuration;
	}

	protected void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	protected Request renameSheet(int sheetId, String title) {
		return new Request().setUpdateSheetProperties(new UpdateSheetPropertiesRequest().setProperties(new SheetProperties().setSheetId(sheetId).setTitle(title)).setFields("title"));
	}

	protected List<Request> getAddSheetRequests(String title, int sheetNumber) {
			List<Request> requests = new ArrayList<>();
			requests.add(
					new Request().setAddSheet(
						new AddSheetRequest().setProperties(
								new SheetProperties().setTitle(title).setIndex(sheetNumber).setGridProperties(
									new GridProperties().setRowCount(new Integer(1000)).setColumnCount(new Integer(20))
								).setSheetId(new Integer(sheetNumber))
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

	protected Request getMergeRequest(int sheetNumber, int startRow, int endRow, int startColumn, int endColumn, String mergeType) {
		return new Request().setMergeCells(
				new MergeCellsRequest().setRange(
						new GridRange().setSheetId(new Integer(sheetNumber)).setStartRowIndex(startRow).setEndRowIndex(endRow).setStartColumnIndex(startColumn).setEndColumnIndex(endColumn)					
					).setMergeType(mergeType)
				);
	}

	protected Request getCellValueAndStyleRequest(String cellValue, int sheetNumber, int startRow, int endRow, int startColumn, int endColumn,
			String style) {
			
				return new Request().setRepeatCell(new RepeatCellRequest()
						.setCell(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(cellValue))
								.setUserEnteredFormat(getCellFormat(style)))
						.setRange(new GridRange().setSheetId( sheetNumber).setStartRowIndex(startRow).setEndRowIndex(endRow)
								.setStartColumnIndex(startColumn).setEndColumnIndex(endColumn))
						.setFields("*"));
			}

	protected CellFormat getCellFormat(String style) {
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

	protected Map<String, String> parseStyle(String style) {
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

	protected Request getSizeCellRequest(int sheetNumber, boolean isDimensionColumn, int startIndex, int endIndex, int pixelSize) {
		return new Request().setUpdateDimensionProperties(
					new UpdateDimensionPropertiesRequest().setRange(
								new DimensionRange().setDimension(isDimensionColumn?"COLUMNS":"ROWS").setSheetId(sheetNumber).setStartIndex(new Integer(startIndex)).setEndIndex(new Integer(endIndex))
							).setProperties(
								new DimensionProperties().setPixelSize(new Integer(pixelSize))
							).setFields("pixelSize")
				);
	}
}
