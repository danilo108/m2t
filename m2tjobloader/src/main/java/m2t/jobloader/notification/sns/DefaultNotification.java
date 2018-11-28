package m2t.jobloader.notification.sns;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Maybe make a component factory that gets all the static information from application.properties
 * and the rest from parameters and calls a defaultSNS MEssage it has to get also the arn from properties
 * maybe also the REGION
 * @author Shreya
 *
 */

public class DefaultNotification extends AbstractM2TNotification {

	private String credentialKey;
	
	private String credentialsSecret;
	
	private String subject;
	
	private String message;
	
	private String topicARN;
	
	public String getCredentialKey() {
		return credentialKey;
	}

	public void setCredentialKey(String credentialKey) {
		this.credentialKey = credentialKey;
	}

	public void setCredentialsSecret(String credentialsSecret) {
		this.credentialsSecret = credentialsSecret;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	protected String getSubject() {
		return subject;
	}

	@Override
	protected String getMessage() {
		return message;
	}

	@Override
	protected String getCrediantlsKey() {
		return credentialKey;
	}

	@Override
	protected String getCredentialsSecret() {
		return credentialsSecret;
	}

	@Override
	protected String getTopicARN() {
		return topicARN;
	}

	public void setTopicARN(String topicARN) {
		this.topicARN = topicARN;
	}
	
}
