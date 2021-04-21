package com.example.demo;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;


@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		//Creating S3 client
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();		
		//Listing Buckets
		List<Bucket> buckets = s3.listBuckets();		
		//iterating through the buckets
		buckets.stream().forEach(bucket ->{
			System.out.println("Bucket Name: " + bucket.getName());
		});

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		DynamoDB dynamoDB = new DynamoDB(client);
		String tableName = "Movies";
		try {			
			Table table=dynamoDB.getTable(tableName);
			if(table.getTableName().equalsIgnoreCase(tableName)) {
				System.out.println("Table found: ");
			}else {
				System.out.println("Attempting to create table; please wait...");
				table = dynamoDB.createTable(tableName,
						Arrays.asList(new KeySchemaElement("year", KeyType.HASH), // Partition key							
								new KeySchemaElement("title", KeyType.RANGE)), // Sort key
						Arrays.asList(new AttributeDefinition("year", ScalarAttributeType.N),
								new AttributeDefinition("title", ScalarAttributeType.S)),
						new ProvisionedThroughput(10L, 10L));

				table.waitForActive();
				System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

			}} catch (Exception  e) {
				System.err.println("Unable to create table: ");
				System.err.println(e.getMessage());
			}


	}

}