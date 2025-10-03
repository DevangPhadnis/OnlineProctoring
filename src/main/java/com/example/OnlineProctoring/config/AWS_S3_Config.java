package com.example.OnlineProctoring.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWS_S3_Config {

    @Value("${aws.accessKey:}")
    private String accessKey;

    @Value("${aws.secretKey:}")
    private String secretKey;


    @Bean
    public AmazonS3 amazonS3() {
        if(accessKey != null && !accessKey.isEmpty() && secretKey != null && !secretKey.isEmpty()) {
            AWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
            return AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();
        }
        else {
            return AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
        }
    }
}
