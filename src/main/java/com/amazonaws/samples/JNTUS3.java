package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class JNTUS3 {
	
	private String htno;
	private String clg;
	private String subject;
	private AmazonS3 s3;
	private String bucketName;
	private String objectKey;
	public JNTUS3(String htno,String clg,String subject)
	{
		this.htno=htno;
		this.clg=clg;
		this.subject=subject;
		this.objectKey=createObjectKey();
		this.bucketName="com.jntu.evaluation."+clg.toLowerCase();
		AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Home\\.aws\\credentials), and is in valid format.",
                    e);
        }

        s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion("us-west-2")
            .build();
        
	}
	public String createObjectKey()
	{
		String object=htno+"-"+clg+"-"+subject;
		return object;
	}
	public void createBucket(String bucketName)
	{
		if(!containsBucket(bucketName))
		{
			s3.createBucket(bucketName);
		}
	}
	public void addObjectToBucket(String bucketName,String object,File file)
	{
		s3.putObject(bucketName, object, file);
	}
	public boolean containsBucket(String buckName)
	{
		boolean isTrue=false;
		for (Bucket bucket : s3.listBuckets()) {
			if(bucketName.equals(bucket.getName()))
			{
				isTrue=true;
				break;
			}
		}
		return isTrue;
	}
	public void addObject(File file)
	{
		createBucket(bucketName);
		addObjectToBucket(bucketName,objectKey,file);
	}
	public void deleteObject(String bucketName,String key)
	{
		System.out.println("Deleting object from "+bucketName+" ->  "+key);
		s3.deleteObject(bucketName, key);
	}

}
