package com.amazonaws.lambda.s3example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileHelper {
	
	static AWSCredentials credentials = new BasicAWSCredentials("AccessKey","SecretKey");
	static AmazonS3 s3client = new AmazonS3Client(credentials);
	
	public static URIResolver getURIResolver() {
		return new URIResolver() {
			@Override
			public Source resolve(String href, String base)
					throws TransformerException {
				InputStream is = getClass().getClassLoader()
						.getResourceAsStream(href);
				return new StreamSource(is);
			}
		};
	}

	public static void uploadToS3(File tempFile, String fileName)
			throws IOException {
		try {
			PutObjectRequest request = new PutObjectRequest("deepaks3test2",
					fileName, tempFile);
			s3client.putObject(request);
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which "
					+ "means your request made it "
					+ "to Amazon S3, but was rejected with an error response"
					+ " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which "
					+ "means the client encountered "
					+ "an internal error while trying to "
					+ "communicate with S3, to " + "deepaks3test2"
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	public static File getFileFromS3Event(S3Event s3Event,Context context){
		for(S3EventNotificationRecord record:s3Event.getRecords()){
			S3Entity entity=record.getS3();
			InputStream in=s3client.getObject(entity.getBucket().getName(), entity.getObject().getKey()).getObjectContent();
			ObjectMapper mapper = new ObjectMapper();
			try {
				final File tempFile = stream2file(in);
				return tempFile;
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	return null;
	}
	
	public static File stream2file (InputStream in) throws IOException {
		
    	final String PREFIX = "stream2file";
        final String SUFFIX = ".tmp";
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }
}

