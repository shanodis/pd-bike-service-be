package me.project.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
public class AmazonConfig {

    public String bucketName = "bike-be-files";

    public String getBucketName() {
        return bucketName;
    }

    public String accessKey = "AKIA6J5SDNORG24EUIN3";
    public String secretKey = "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";

    AWSCredentials credentials = new BasicAWSCredentials(
            accessKey,
            secretKey
    );

    @Bean
    AmazonS3 s3client(){
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
    }
}
