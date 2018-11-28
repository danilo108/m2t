package m2t.jobloader.notification.sns;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

public class Test {
	
	public static void main(String[] args) {
		AWSCredentials credentials = 
				new BasicAWSCredentials("AKIAJTEFFJCMKEEEPQVQ", "GNXm5R+KYe75ei2pIGHJxurV/efPs2oa51iiktMr");
		AmazonSNS sns = AmazonSNSClient.builder().withCredentials(new StaticCredentialsProvider(credentials)).
				withRegion(Regions.US_EAST_1).build();
		sns.publish("arn:aws:sns:us-east-1:979994616735:m2tDeliveryManager", "Hi, the container 5130B is ready for planning click here to change the delivery codes https://docs.google.com/spreadsheets/d/1xXK_N27BYCDWls7UJawGatVuEIgw4lzBol-MqBb4Rtg/edit#gid=0  and here to generate the report http://www.google.it");
	}

}
