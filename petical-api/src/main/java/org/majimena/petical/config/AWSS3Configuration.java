package org.majimena.petical.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.extern.slf4j.Slf4j;
import org.majimena.petical.common.aws.AmazonS3Service;
import org.majimena.petical.common.aws.impl.AmazonS3ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWSのコンフィグレーション.
 */
@Slf4j
@Configuration
@AutoConfigureAfter(value = {AWSAuthenticationConfiguration.class})
public class AWSS3Configuration {

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.type}")
    private String type;

    @Autowired
    private AWSCredentialsProvider awsCredentialsProvider;

    @Bean
    public AmazonS3Service amazonS3Service() {
        log.info("AWS S3 AccessKey={}", awsCredentialsProvider.getCredentials().getAWSAccessKeyId());

        AmazonS3Client client = new AmazonS3Client(awsCredentialsProvider);
        client.setRegion(Region.getRegion(Regions.fromName(region)));

        AmazonS3ServiceImpl service = new AmazonS3ServiceImpl();
        service.setAmazonS3Client(client);
        service.setBucketName(bucket);
        service.setContentType(type);
        return service;
    }
}
