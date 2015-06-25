package org.majimena.petz.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import org.majimena.framework.aws.AmazonSESEmailManager;
import org.majimena.framework.core.managers.EmailManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by todoken on 2015/06/22.
 */
@Configuration
public class AmazonSESConfiguration {

    @Bean
    public EmailManager emailManager() {
        AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(new DefaultAWSCredentialsProviderChain());
        Region REGION = Region.getRegion(Regions.US_WEST_2);
        client.setRegion(REGION);

        AmazonSESEmailManager manager = new AmazonSESEmailManager();
        manager.setAmazonSimpleEmailService(client);
        return manager;
    }

}
