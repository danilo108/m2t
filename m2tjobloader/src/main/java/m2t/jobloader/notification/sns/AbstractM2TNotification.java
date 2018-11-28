package m2t.jobloader.notification.sns;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.mediastoredata.model.PutObjectRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public abstract class AbstractM2TNotification implements M2TNotification{

	@Override
	public void send() {
		AmazonSNS client = getClient();
		PublishRequest publishRequest = new PublishRequest();
		publishRequest.setSubject(getSubject());
		publishRequest.setMessage(getMessage());
		publishRequest.setTopicArn(getTopicARN());
		PublishResult response = client.publish(publishRequest );
		
		
	}


	protected abstract String getTopicARN();


	protected AmazonSNS getClient() {
		AWSCredentials credentials = 
				new BasicAWSCredentials(getCrediantlsKey(), getCredentialsSecret());
		AmazonSNS sns = AmazonSNSClient.builder().withCredentials(new StaticCredentialsProvider(credentials)).
				withRegion(getRegion()).build();
		return sns;
	}

	protected Regions getRegion() {
		return Regions.US_EAST_1;
	}

	
	protected abstract String getSubject();

	protected abstract String getMessage();

	protected abstract String getCrediantlsKey();

	protected abstract String getCredentialsSecret();

}
