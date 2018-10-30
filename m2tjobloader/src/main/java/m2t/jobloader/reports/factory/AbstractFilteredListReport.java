package m2t.jobloader.reports.factory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;

import m2t.jobloader.service.controllers.GoogleWrapper;
import m2t.jobloader.service.controllers.model.ResponseErrorDetail;

@Component
public abstract class AbstractFilteredListReport<T> extends BasicBatchRequestFactory implements FilteredListReportFactory<T> {
	private static final String STYLE_COLUMN_WIDTH = "columnWidth";
	protected List<T> originalList;
	protected List<T> filteredList;
	@Autowired
	GoogleWrapper googleWrapper;
	private String sheetId;
	private int sheetNumber;
	private String sheetURL;
	private Map<String, String> titlePropertyMap;
	
	public AbstractFilteredListReport() {
		titlePropertyMap = initialiseTitlePropertyMap();
	}
	
	


	@Override
	public ReportFactoryResponse buildReport(List<T> list, Predicate<? super T> predicate, Comparator<? super T> sortComparator) {
		this.originalList = list.stream().collect(Collectors.toList());
		this.filteredList = list.stream().filter(predicate).
				sorted(sortComparator).collect(Collectors.toList());
		ReportFactoryResponse response = createNewResponse("MAIN");
		ReportFactoryResponse preRequisitOperation = executePreRequisitOperation();
		response.addOperationResponse(preRequisitOperation);
		if(preRequisitOperation.isError()) {
			return response;
		}
		sheetId = getSheetId(preRequisitOperation);
		
		response.setSheetId(getSheetId());
		response.setFullURL(getSheetURL());
		response.addOperationResponse( preRequisitOperation);
		sheetNumber = preRequisitOperation.getSheetNumber();
		
		List<Request> requests = new ArrayList<>();
		for(int tIndex = 0; tIndex < this.filteredList.size(); tIndex++) {
			Map<String, String> rowMap = translateRowObjectToMap(list.get(tIndex));
			if(tIndex == 0) {
				//Add Title
				requests.addAll(addTitle(rowMap, sheetNumber));
				requests.addAll(addColumnWidhts(rowMap, sheetNumber, response));
			}
			//Add each column
			for(String key:rowMap.keySet()) {
				Integer columnNumber = getColumnNumberForRowProperty(key);
				if(columnNumber != null) {
					String cellStyle = getCellStyleForProperty(key, rowMap, tIndex, columnNumber.intValue());
					String value = rowMap.get(key);
					int startRow = decideStartRowFor(key, rowMap, tIndex, columnNumber.intValue());
					requests.add(getCellValueAndStyleRequest(value, sheetNumber, startRow , startRow + 1 , columnNumber, columnNumber + 1, cellStyle));
					
				}
				
			}
		}
		
		ReportFactoryResponse writeRows = executeWriteRows(requests);
		response.addOperationResponse(writeRows);
		if(writeRows.isError()) {
			response.setError(true);
			response.setErrorDescription("Error while writing the rows");
			return response;
		}
		ReportFactoryResponse postOperation = executePostRequisitOperations();
		if(postOperation != null && postOperation.isError()) {
			response.setError(true);
			response.setErrorDescription("Error on the post requisit operation");
			
		}
		return response;
	}




	private ReportFactoryResponse createNewResponse(String operation) {
		ReportFactoryResponse response = new ReportFactoryResponse(operation);
		response.setSheetId(getSheetId());
		response.setFullURL(getSheetURL());
		return response;
	}



	protected ReportFactoryResponse executePostRequisitOperations() {
		List<Request> requests = getPostRequisitOperationsRequests();
		if(!requests.isEmpty()) {
			return executeRequests(requests , "Post requisits");
		}else {
			return null;
		}
	}



	protected List<T> getOriginalList() {
		return originalList;
	}




	protected void setOriginalList(List<T> originalList) {
		this.originalList = originalList;
	}




	protected List<T> getFilteredList() {
		return filteredList;
	}




	protected void setFilteredList(List<T> filteredList) {
		this.filteredList = filteredList;
	}




	protected GoogleWrapper getGoogleWrapper() {
		return googleWrapper;
	}




	protected void setGoogleWrapper(GoogleWrapper googleWrapper) {
		this.googleWrapper = googleWrapper;
	}




	public String getSheetURL() {
		return sheetURL;
	}




	public void setSheetURL(String sheetURL) {
		this.sheetURL = sheetURL;
	}




	protected Map<String, String> getTitlePropertyMap() {
		return titlePropertyMap;
	}




	protected void setTitlePropertyMap(Map<String, String> titlePropertyMap) {
		this.titlePropertyMap = titlePropertyMap;
	}




	protected List<Request> getPostRequisitOperationsRequests() {
		return new ArrayList<>();
	}



	protected ReportFactoryResponse executeWriteRows(List<Request> requests) {
		
		return executeRequests(requests, "Write rows");
	}



	public String getSheetId() {
		return sheetId;
	}



	public void setSheetId(String sheetId) {
		this.sheetId = sheetId;
	}



	public void setSheetNumber(int sheetNumber) {
		this.sheetNumber = sheetNumber;
	}



	protected ReportFactoryResponse executePreRequisitOperation() {
		ReportFactoryResponse createSheetOp = createSheet();
		if(createSheetOp.isError()) {
			return createSheetOp;
		}
		return createSheetOp;
	}
	
	

	private List<Request> addColumnWidhts(Map<String, String> rowMap, int sheetNumber, ReportFactoryResponse response) {
		List<Request> requests = new ArrayList<>();
		for(String key:rowMap.keySet()) {
			Integer columnNumber = getColumnNumberForRowProperty(key);
			if(columnNumber != null) {
				String cellStyle = getCellStyleForTitle(key, columnNumber.intValue());
				Map<String, String> styleMap = parseStyle(cellStyle);
				if(styleMap.containsKey(STYLE_COLUMN_WIDTH)) {
					try {
						int pixelSize = Integer.parseInt(styleMap.get(STYLE_COLUMN_WIDTH));
						requests.add(getSizeCellRequest(sheetNumber, true, columnNumber, columnNumber+1, pixelSize));
					} catch (NumberFormatException e) {
						response.getWarnings().add(new ResponseErrorDetail("ERROR", "error while parsing the column width of the column number " + columnNumber + " for the report " + getReportName(), cellStyle));
					}
				}
			}
			
		}
		return requests;
	}



	


	protected int decideStartRowFor(String key, Map<String, String> rowMap, int tIndex, int intValue) {
		return getFirstRow() + getTitleRows() + tIndex;
	}


	/**
	 * 
	 * @return the name of the report
	 */
	protected abstract String getReportName();


	/**
	 * 
	 * @return the number of rows used for the title. Override if you need to start adding rows to the table after the title
	 */
	protected int getTitleRows() {
		return 1;
	}

	/**
	 * 
	 * @return a map with key property Name and value the corresponding title.
	 * if the map is not initialised or a property is not set, it will return an empty string
	 */
	protected abstract Map<String, String> initialiseTitlePropertyMap();


	private List<Request> addTitle(Map<String, String> rowMap, int sheetNumber) {
			List<Request> requests = new ArrayList<>();
			for(String key:rowMap.keySet()) {
				Integer columnNumber = getColumnNumberForRowProperty(key);
				if(columnNumber != null) {
					String cellStyle = getCellStyleForTitle(key, columnNumber.intValue());
					String value = getTitleForProperty(key);
					
					
					int startRow = getFirstRow();
					int endRow = startRow + 1;
					requests.add(getCellValueAndStyleRequest(value, sheetNumber, startRow , endRow , columnNumber, columnNumber + 1, cellStyle));
				}
				
			}
			return requests;
	}

	

	/**
	 * THe cell style related to the cell which could be identified with the property of the rowObject,
	 * or columnNumber. This method allows to apply conditional style
	 * @param property
	 * @param rowMap
	 * @param rowNumber
	 * @param columnNumber
	 * @return the pseudo css3 style
	 */
	protected abstract String getCellStyleForTitle(String key, int intValue);



	/**
	 * From the propertyName of the RowObject define the title
	 * @param propertyName
	 * @return the corresponsding title'
	 */
	protected String getTitleForProperty(String propertyName) {
		if(titlePropertyMap == null) {
			titlePropertyMap = new HashMap<>();
		}
		if(titlePropertyMap.containsKey(propertyName)) {
			return titlePropertyMap.get(propertyName);
		}else{
			return "";
		}
	}



	/**
	 * The first row where the report start writing including the title
	 * @return 0 if it is the first line
	 */
	protected abstract int getFirstRow();



	protected String getSheetId(ReportFactoryResponse createSheetOp) {
		
		return createSheetOp.getSheetId();
	}



	protected ReportFactoryResponse createSheet() {
		List<Request> requests = getAddSheetRequests(getReportName(), getSheetNumber());
		String operationName = "Create Sheet";
		return executeRequests(requests, operationName);
	}



	private ReportFactoryResponse executeRequests(List<Request> requests, String operationName) {
		ReportFactoryResponse response = createNewResponse(operationName);
		
		 try {
			BatchUpdateSpreadsheetResponse batchUpdateRes = googleWrapper.executeBatchUpdate(getSheetId(), requests);
			response.setSheetId(batchUpdateRes.getSpreadsheetId());
			
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
			response.setError(true);
			response.setErrorDescription("Error while executing the operation " + operationName);
			response.getWarnings().add(new ResponseErrorDetail("ERROR", "Error while executing the operation " + operationName + " - " + e.getMessage(), requests, e));
			
		}finally {
			return response;
		}
	}



	/**
	 * 
	 * @return the sheet Number for the report. Note that it must be unique
	 */
	protected abstract int getSheetNumber();



	/**
	 * THe cell style related to the cell which could be identified with the property of the rowObject,
	 * or the rowNumber and/or columnNumber. This method allows to apply conditional style
	 * @param property
	 * @param rowMap
	 * @param rowNumber
	 * @param columnNumber
	 * @return the pseudo css3 style
	 */
	protected abstract String getCellStyleForProperty(String property, Map<String, String> rowMap, int rowNumber, int columnNumber);

	/**
	 * The concrete class has to define which column corresponds to the property
	 * @param propertyName the name of the property 
	 * @return null if the property shouldn't be inserted in the report, otherwise the number of the column starting from 0
	 */
	protected abstract Integer getColumnNumberForRowProperty(String propertyName);

	/**
	 * Conver the object value in the userEnteredMode values to display of the column. Note that this is the
	 * place where the value of the cell can be calculated based on rowObject
	 * @param rowObject the object as an element of the list in input
	 * @return
	 */
	protected abstract Map<String, String> translateRowObjectToMap(T rowObject);
	
	
	
}
