package com.amazonaws.lambda.s3example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.JsonNode;

public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {

    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

    public LambdaFunctionHandler() {}

    // Test purpose only.
    LambdaFunctionHandler(AmazonS3 s3) {
        this.s3 = s3;
    }

    @Override
    public String handleRequest(S3Event input, Context context) {
        
    	try {
    		
    		context.getLogger().log("Input: " + input);
    		S3EventNotificationRecord record = input.getRecords().get(0);
            String srcKey = record.getS3().getObject().getKey();
			FileHelper fileHelper=new FileHelper();
			File obj = fileHelper.getFileFromS3Event(input, context);
			fileHelper.uploadToS3(obj, srcKey);
			return "success";
    		 
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    

}