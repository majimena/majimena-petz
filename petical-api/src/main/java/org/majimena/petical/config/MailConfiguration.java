package org.majimena.petical.config;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.common.provider.EmailProvider;
import org.majimena.petical.common.provider.impl.AmazonSESEmailProviderImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;

@Configuration
@AutoConfigureAfter(value = {AmazonWebServiceConfiguration.class, ThymeleafConfiguration.class})
public class MailConfiguration implements EnvironmentAware {

    private RelaxedPropertyResolver propertyResolver;

    @Inject
    private SpringTemplateEngine springTemplateEngine;

    @Inject
    private AmazonSimpleEmailServiceAsyncClient amazonSimpleEmailServiceAsyncClient;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment);
    }

    @Bean
    public EmailProvider emailProvider() {
        // メールプロバイダの切り替え
        String name = propertyResolver.getProperty("email.provider");
        if (StringUtils.equals("SES", name)) {
            // AWS SESを使う
            AmazonSESEmailProviderImpl provider = new AmazonSESEmailProviderImpl();
            provider.setAmazonSimpleEmailService(amazonSimpleEmailServiceAsyncClient);
            provider.setTemplateEngine(springTemplateEngine);
            provider.setName(propertyResolver.getProperty("email.name"));
            provider.setFrom(propertyResolver.getProperty("email.from"));
            provider.setCharset(propertyResolver.getProperty("email.charset"));
            return provider;
        } else {
            // SendGridを使う
            return null;
        }
    }
}
