package m2t.jobloader.notification.sns;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import m2t.jobloader.service.controllers.model.BasicServiceResponse;
import m2t.jobloader.service.controllers.model.PrintReportResponse;

@Component
public class NotificationFactory {

	@Value("${m2t.aws.sns.docketMess.secret}")
	private String secret;

	@Value("${m2t.aws.sns.docketMess.key}")
	private String key;

	@Value("${m2t.aws.sns.docketMess.messages.sheetCreatedForDeliveryManager}")
	private String sheetCreatedForDeliveryManagerMessage;
	@Value("${m2t.aws.sns.docketMess.messages.sheetCreatedForInstallerManager}")
	private String sheetCreatedForInstallerManagerMessage;

	@Value("${m2t.aws.sns.docketMess.messages.sheetCreatedForDeliveryManager.subject}")
	private String sheetCreatedForDeliveryManagerSubject;
	@Value("${m2t.aws.sns.docketMess.topics.deliveryManager.arn}")
	private String deliveryManagerTopicARN;
	@Value("${m2t.aws.sns.docketMess.topics.installerManager.arn}")
	private String installerManagerTopicARN;


	@Value("${m2t.report.downloadReport.URL}")
	private String downloadReportURL;

	public M2TNotification createSheetCreatedForDeliveryManager(String containerNumber, String sheetUrl,
			String generateReportURL) {
		String message = sheetCreatedForDeliveryManagerMessage.replaceAll("__containerNumber__", containerNumber)
				.replaceAll("__generateReportURL__", generateReportURL).replaceAll("__sheetUrl__", sheetUrl);
		String subject = sheetCreatedForDeliveryManagerSubject.replaceAll("__containerNumber__", containerNumber)
				.replaceAll("__generateReportURL__", generateReportURL).replaceAll("__sheetUrl__", sheetUrl);
		return createDefaultNotification(message, subject, deliveryManagerTopicARN);
	}

	public M2TNotification createSheetCreatedForInstallersManager(String containerNumber, String sheetUrl) {
		String message = sheetCreatedForInstallerManagerMessage.replaceAll("__containerNumber__", containerNumber)
				.replaceAll("__sheetUrl__", sheetUrl);
		String subject = sheetCreatedForDeliveryManagerSubject.replaceAll("__containerNumber__", containerNumber)
				.replaceAll("__sheetUrl__", sheetUrl);
		return createDefaultNotification(message, subject, deliveryManagerTopicARN);
	}

	private M2TNotification createDefaultNotification(String message, String subject, String topicARN) {
		DefaultNotification notification = new DefaultNotification();
		notification.setCredentialKey(key);
		notification.setCredentialsSecret(secret);
		notification.setTopicARN(topicARN);
		notification.setMessage(message);
		notification.setSubject(subject);
		return notification;
	}

	public M2TNotification createFloorReportPrintNotification(PrintReportResponse response) {
		StringBuffer message = new StringBuffer();
		message.append("You asked to print the floor report for the container ");
		message.append(response.getContainerNumber());
		message.append(". ");
		if (response.isError()) {
			message.append(". Unfortunately, the operation went WRONG!!! ");
			message.append(response.getErrorDescription());
			if (!StringUtils.isBlank(response.getOperationWithException())) {
				BasicServiceResponse subOperation = response.getOperations().get(response.getOperationWithException());
				if (subOperation != null && StringUtils.isNotBlank(subOperation.getErrorDescription())) {
					message.append(". More precisily: ");
					message.append(subOperation.getErrorDescription());
					if (subOperation.getWarnings().size() > 0) {
						message.append(" - ");
						message.append(subOperation.getWarnings().get(0).getErrorDescription());
						message.append(". ");
					}
				}

			}
		} else {
			if (response.getUncompletedJobs() > 0) {

				message.append("BE AWARE that there are ");
				message.append(response.getUncompletedJobs());
				message.append(
						" that haven't be allocated to any installer or confirmed to VEROSOL.");
//						You can reprint again the report once you complete to update the spreadsheet at the following address ");
//				message.append(response.getSpreadSheetFullURL());
			}
			message.append(" You can download the pdf at the following address: ");
			message.append(downloadReportURL);
			message.append(response.getContainerNumber());
			message.append("          ADVANCED actions: If you want to edit the report before to print it click here: ");
			message.append(response.getReportSheetFullURL());
			
			
		}
		String subject = "Floor report - " + response.getContainerNumber();
		return createDefaultNotification(message.toString(), subject, deliveryManagerTopicARN);

	}
}
