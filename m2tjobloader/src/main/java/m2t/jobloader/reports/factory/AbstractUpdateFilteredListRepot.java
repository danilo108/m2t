package m2t.jobloader.reports.factory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.ValueRange;

import m2t.jobloader.service.controllers.model.BasicServiceResponse;
import m2t.jobloader.service.controllers.model.ResponseErrorDetail;
import m2t.jobloader.service.controllers.model.ReverseReportUpdateResponse;

@Component
public abstract class AbstractUpdateFilteredListRepot<DAO, DTO> extends AbstractFilteredListReport<DAO, DTO>
		implements ReverseReportUpdate {

	
	public AbstractUpdateFilteredListRepot() {
		super();
	}
	
	@Override
	public ReverseReportUpdateResponse update() {
		ReverseReportUpdateResponse response = new ReverseReportUpdateResponse("Update " + getReportName());
		List<SheetRowUpdate<DTO>> updates = getUpdates(response);
		if(updates == null) {
			return response;
		}
		for (SheetRowUpdate<DTO> update : updates) {
			BasicServiceResponse updateResponse = processRowUpdate(update);
			response.addOperationResponse(updateResponse);
			if(!response.isError() && updateResponse.isError()) {
				response.setError(true);
				response.setErrorDescription("Error while updating the dto ");
				response.addWarning(new ResponseErrorDetail("Error", "Error while updating the dto", update));
			}
		}

		return response;
	}

	private BasicServiceResponse processRowUpdate(SheetRowUpdate<DTO> update) {
		if(update.getOperation().equals(SheetRowUpdate.OPERATIONS.DELETE)) {
			return deleteDTO( update.getDto());
		}else if(update.getOperation().equals(SheetRowUpdate.OPERATIONS.ADD)) {
			return addDTO( update.getDto());
		}else{
			return updateDTO( update.getDto());
		}
	}



	

	protected List<SheetRowUpdate<DTO>> getUpdates(ReverseReportUpdateResponse response) {

		ValueRange valueRange;
		try {
			valueRange = googleWrapper.getValueRange(getSheetId(), getA1ValuesRange());
		} catch (IOException | GeneralSecurityException e2) {
			e2.printStackTrace();
			response.setError(true);
			response.setErrorDescription(
					"Error gettting the values from  " + getA1ValuesRange());
			response.addWarning(new ResponseErrorDetail("ERROR",
					"Error gettting the values from  " + getA1ValuesRange(), "", e2));
			return null;
		}
		List<SheetRowUpdate<DTO>> updates = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < valueRange.getValues().size(); rowIndex++) {

			List<Object> row = valueRange.getValues().get(rowIndex);
			if (row.size() < getTranslator().getColumns().size()) {
				response.setError(true);
				response.setErrorDescription(
						"Some rows could not be processed because some cells are blank. Put a space in the blank cells in the range "
								+ getA1ValuesRange() + " at row " + rowIndex);
				response.addWarning(new ResponseErrorDetail("ERROR",
						"The row does not have all the required fields, some may be blanks. Replace blanks cell with a space at row "
								+ rowIndex,
						row));
				continue;
			}
			try {
				SheetRowUpdate<DTO> update = instantiateRowUpdate(row);
				Map<String, String> rowMap;
				try {
					rowMap = translateRowInMap(row);
				} catch (Exception e1) {
					e1.printStackTrace();
					response.setError(true);
					response.setErrorDescription(
							"Error translating the row in map " + getA1ValuesRange() + " at row " + rowIndex);
					response.addWarning(new ResponseErrorDetail("ERROR",
							"Error translating the row in map " + getA1ValuesRange() + " at row " + rowIndex, row));
					continue;
				}
				try {
					DTO dto = getTranslator().toDTO(rowMap);
					update.setDto(dto);
				} catch (Exception e) {
					e.printStackTrace();
					response.setError(true);
					response.setErrorDescription("Error while translating the map in DTO at row " + rowIndex);
					response.addWarning(new ResponseErrorDetail("ERROR",
							"Error while translating the map in DTO at row " + rowIndex, rowMap));
					continue;
				}
				updates.add(update);
			} catch (Exception e1) {
				e1.printStackTrace();
				response.setError(true);
				response.setErrorDescription(
						"Error translating the row in SheetRowUpdate " + getA1ValuesRange() + " at row " + rowIndex);
				response.addWarning(new ResponseErrorDetail("ERROR",
						"Error translating the row in SheetRowUpdate " + getA1ValuesRange() + " at row " + rowIndex, row));
				continue;
			}
			Map<String, String> rowMap = null;
			
		}
		return updates;
	}

	private SheetRowUpdate<DTO> instantiateRowUpdate(List<Object> row) {
		SheetRowUpdate<DTO> update = new SheetRowUpdate<>();
		update.setOperation(SheetRowUpdate.OPERATIONS.UPDATE);
		if (row.size() == getTranslator().getColumns().size() + 1) {
			Object operation = row.get(row.size() - 1);
			if (operation != null && (operation.toString().toUpperCase().contains("DEL")
					|| operation.toString().toUpperCase().contains("REM"))) {
				update.setOperation(SheetRowUpdate.OPERATIONS.DELETE);
			} else if (operation != null && (operation.toString().toUpperCase().contains("ADD")
					|| operation.toString().toUpperCase().contains("CREA"))) {
				update.setOperation(SheetRowUpdate.OPERATIONS.ADD);
			}
		}
		return update;
	}

	private Map<String, String> translateRowInMap(List<Object> row) {
		Map<String, String> rowMap = new HashMap<>();
		for (int index = 0; index < getTranslator().getColumns().size(); index++) {
			if (row.get(index) != null) {
				rowMap.put(getTranslator().getColumns().get(index), row.get(index).toString());
			}
		}
		return rowMap;
	}

	/**
	 * 
	 * @return the A1 notation for the values to get for the updates. Remember to
	 *         add the extra column for the operation DELETE or ADD otherwise the
	 *         default operation is UPDATE
	 */
	protected abstract String getA1ValuesRange();

	/**
	 * Calls the controller service to delete the entity described in the dto
	 * @param dto
	 * @return the service response
	 */
	protected abstract BasicServiceResponse deleteDTO(DTO dto);
	/**
	 * Calls the controller service to add the entity described in the dto
	 * @param dto
	 * @return the service response
	 */
	protected abstract BasicServiceResponse addDTO(DTO dto);
	/**
	 * Calls the controller service to update the entity described in the dto
	 * @param dto
	 * @return the service response
	 */
	protected abstract BasicServiceResponse updateDTO(DTO dto);


}
