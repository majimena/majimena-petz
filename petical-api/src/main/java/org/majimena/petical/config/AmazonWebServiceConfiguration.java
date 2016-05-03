package org.majimena.petical.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import org.majimena.petical.common.aws.AmazonS3Service;
import org.majimena.petical.common.aws.impl.AmazonS3ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by todoken on 2015/06/22.
 */
@Configuration
public class AmazonWebServiceConfiguration implements EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonWebServiceConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment env) {
        this.propertyResolver = new RelaxedPropertyResolver(env);
    }

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new DefaultAWSCredentialsProviderChain();
    }

    @Bean
    @Autowired
    public AmazonSimpleEmailServiceAsyncClient amazonSESService(AWSCredentialsProvider provider) {
        LOGGER.info("AWS SES AccessKey={}", provider.getCredentials().getAWSAccessKeyId());

        AmazonSimpleEmailServiceAsyncClient client = new AmazonSimpleEmailServiceAsyncClient(provider);
        client.setRegion(Region.getRegion(Regions.fromName(propertyResolver.getProperty("aws.ses.region"))));
        return client;
    }

    @Bean
    @Autowired
    public AmazonS3Service amazonS3Service(AWSCredentialsProvider provider) {
        LOGGER.info("AWS S3 AccessKey={}", provider.getCredentials().getAWSAccessKeyId());

        AmazonS3Client client = new AmazonS3Client(provider);
        client.setRegion(Region.getRegion(Regions.fromName(propertyResolver.getProperty("aws.s3.region"))));

        AmazonS3ServiceImpl service = new AmazonS3ServiceImpl();
        service.setAmazonS3Client(client);
        service.setBucketName(propertyResolver.getProperty("aws.s3.bucket", "images"));
        service.setContentType(propertyResolver.getProperty("aws.s3.type", "image/jpeg"));
        return service;
    }
}
