package m2t.jobloader.service.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Copy;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Create;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.Append;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

import m2t.jobloader.configuration.Configuration;
import m2t.jobloader.dao.model.Job;
import m2t.test.sheet.SheetsQuickstart;

@Component
public class GoogleWrapper {

	private static final String FILE_INFO_WEB_LINK = "id,thumbnailLink,webContentLink,webViewLink";
	@Autowired
	Configuration configuration;

	public Sheets getSheets( ) throws GeneralSecurityException, IOException {
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
		NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	                .setApplicationName(configuration.getApplicationName())
	                .build();
		return service;
	}

	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		GoogleCredential credential = GoogleCredential
				.fromStream(SheetsQuickstart.class.getResourceAsStream(configuration.getCredentialFilePath()))
				.createScoped(SheetsScopes.all());
		return credential;
	}
	
	public Drive getDriveService() throws GeneralSecurityException, IOException {
		NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		return  new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(configuration.getApplicationName())
                .build();

	}


	public File duplicateFromTemplate(String templateId, String title, List<Permission> permissions) throws IOException, GeneralSecurityException {
		//416941295201-vl3k5sbvvh6qe42og1cgokvlb0tcgidb.apps.googleusercontent.com (clientId)
		//OoSGb0NLRAsmQct2vjWS2Wz8 (secret)
//		String sheetId = createNewSheet();
		
		
		File copiedFile = new File();
		copiedFile.setName(title);
//		copiedFile.setShared(true);
//		copiedFile.setWritersCanShare(true);
//		copiedFile.setPermissions(permissions);
		Copy copyRequest = getDriveService().files().copy(templateId, copiedFile);
		copyRequest.setFields("*");
		File result = copyRequest.execute();
		com.google.api.services.drive.Drive.Permissions.List permreq = getDriveService().permissions().list(templateId);
		PermissionList permres = permreq.execute();
		for(Permission p : permissions) {
			
			com.google.api.services.drive.Drive.Permissions.Create create = getDriveService().permissions().create(result.getId(), p);
			if("owner" == p.getRole()) {
//				create.setTransferOwnership(true);
				continue;
				
			}else {
				create.setSendNotificationEmail(false);
			}
			create.execute();
			
		}
		File fileInfo = getFileInfo(FILE_INFO_WEB_LINK, result.getId());
		return fileInfo;
	}

	public File getFileInfo(String fieldsToVisualise, String id) throws IOException, GeneralSecurityException {
		
		return getDriveService().files().get(id).setFields(fieldsToVisualise).execute();
	}

//	public List<String> getPermissionIds(String templateId) {
//		getDriveService().files()
//		return null;
//	}

	public String createNewSheet() throws IOException, GeneralSecurityException {
		Create request = getSheets().spreadsheets().create(new Spreadsheet());
		return  request.execute().getSpreadsheetId();
	}

	

	ValueRange getValueRange(String sheetId, String googleSheetTemplateJobSheetUpdatesRange) {
		return null;
	}

	public AppendValuesResponse updateRange(String sheetId, String range, List<List<Object>> values) throws IOException, GeneralSecurityException {
		ValueRange 	valueRange = new ValueRange();
		valueRange.setValues(values);
		Append request = getSheets().spreadsheets().values().append(sheetId, range, valueRange);
		request.setIncludeValuesInResponse(true);
		request.setInsertDataOption("OVERWRITE");
		request.setValueInputOption("USER_ENTERED");
		return request.execute();
		
	}

}
