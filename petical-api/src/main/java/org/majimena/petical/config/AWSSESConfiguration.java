package org.majimena.petical.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import lombok.extern.slf4j.Slf4j;
import org.majimena.petical.common.provider.EmailProvider;
import org.majimena.petical.common.provider.impl.AmazonSESEmailProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;

/**
 * AWSのコンフィグレーション.
 */
@Slf4j
@Configuration
@AutoConfigureAfter(value = {AWSAuthenticationConfiguration.class})
public class AWSSESConfiguration {

    @Value("${aws.ses.region}")
    private String region;

    @Value("${app.mail.name}")
    private String name;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.charset}")
    private String charset;

    @Autowired
    private AWSCredentialsProvider awsCredentialsProvider;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Bean
    public AmazonSimpleEmailServiceAsyncClient amazonSimpleEmailServiceAsyncClient() {
        log.info("AWS SES AccessKey={}", awsCredentialsProvider.getCredentials().getAWSAccessKeyId());

        AmazonSimpleEmailServiceAsyncClient client = new AmazonSimpleEmailServiceAsyncClient(awsCredentialsProvider);
        client.setRegion(Region.getRegion(Regions.fromName(region)));
        return client;
    }

    @Bean
    public EmailProvider emailProvider() {
        AmazonSESEmailProviderImpl provider = new AmazonSESEmailProviderImpl();
        provider.setAmazonSimpleEmailService(amazonSimpleEmailServiceAsyncClient());
        provider.setTemplateEngine(springTemplateEngine);
        provider.setName(name);
        provider.setFrom(from);
        provider.setCharset(charset);
        return provider;
    }
}
