package com.amazonaws.samples;
import java.time.LocalDate;
import java.time.LocalDateTime;
/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.AmazonSimpleDBClientBuilder;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;

/**
 * This sample demonstrates how to make basic requests to Amazon SimpleDB using
 * the AWS SDK for Java.
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web Services developer
 * account, and be signed up to use Amazon SimpleDB. For more information on
 * Amazon SimpleDB, see http://aws.amazon.com/simpledb.
 * <p>
 * <b>Important:</b> Be sure to fill in your AWS access credentials in the
 *                   AwsCredentials.properties file before you try to run this
 *                   sample.
 * http://aws.amazon.com/security-credentials
 */
public class SimpleDBEval {
	private String htno;
	private String subject;
	private String objectID;
	private LocalDateTime doc;
	private AmazonSimpleDB sdb;
	private String clg;
	private String myDomain;
	public SimpleDBEval(String htno,String clg,String subject,LocalDateTime doc)
	{
		this.htno=htno;
		this.clg=clg;
		this.subject=subject;
		this.doc=doc;
		this.objectID=htno+"-"+clg+"-"+subject;
		this.myDomain = "Evaluation";
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Home\\.aws\\credentials), and is in valid format.",
                    e);
        }
        sdb=AmazonSimpleDBClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-west-2")
                .build();
       
	}
	public void addDetails()
	{
		try {
            String myDomain = "Evaluation";
            System.out.println("Putting data into " + myDomain + " domain.\n");
            sdb.batchPutAttributes(new BatchPutAttributesRequest(myDomain, createSampleData()));
            }
			catch (AmazonServiceException ase) {
				System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon SimpleDB, but was rejected with an error response for some reason.");
				System.out.println("Error Message:    " + ase.getMessage());
				System.out.println("HTTP Status Code: " + ase.getStatusCode());
				System.out.println("AWS Error Code:   " + ase.getErrorCode());
				System.out.println("Error Type:       " + ase.getErrorType());
				System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with SimpleDB, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
    private List<ReplaceableItem> createSampleData() {
        List<ReplaceableItem> sampleData = new ArrayList<ReplaceableItem>();
        sampleData.add(new ReplaceableItem(UUID.randomUUID().toString()).withAttributes(
                new ReplaceableAttribute("HTNO", htno, true),
                new ReplaceableAttribute("College", clg, true),
                new ReplaceableAttribute("subject", subject, true),
                new ReplaceableAttribute("OID", objectID, true),
                new ReplaceableAttribute("DOC", doc.toString(), true)));
        
        return sampleData;
    }
    public String deleteRecord(String itemNo)
    {
    	String oid=null;
    	System.out.println("Deleting "+itemNo+".\n");
        String selectExpression = "select OID from `" + myDomain+"`";
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        for (Item item : sdb.select(selectRequest).getItems()) {
            String temp_itemNo=item.getName();
            if(temp_itemNo.equals(itemNo))
            {
            	for (Attribute attribute : item.getAttributes()) {
                	oid=attribute.getValue();
                }
            }
        }
        System.out.println(oid);
        sdb.deleteAttributes(new DeleteAttributesRequest(myDomain, itemNo));
        return oid;
    }
    public String findWeekExpire()
    {
    	String oid=null;
    	String selectExpression = "select DOC from `" + myDomain+"`";
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        for (Item item : sdb.select(selectRequest).getItems()) {
            String itemNo=item.getName();
            for (Attribute attribute : item.getAttributes()) {
                LocalDateTime doc=LocalDateTime.parse(attribute.getValue());
                LocalDate now=LocalDate.now();
                if(!doc.toLocalDate().isAfter(now.minusWeeks(1)) )
                {
                	oid=deleteRecord(itemNo);
                }
                
            }
        }
        return oid;
    }
}
