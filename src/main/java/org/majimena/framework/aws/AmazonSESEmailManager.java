package org.majimena.framework.aws;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import org.majimena.framework.core.managers.EmailManager;
import org.majimena.petz.common.exceptions.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

/**
 * Created by todoken on 2015/06/22.
 */
public class AmazonSESEmailManager implements EmailManager {

    private final Logger logger = LoggerFactory.getLogger(AmazonSESEmailManager.class);

    private AmazonSimpleEmailService amazonSimpleEmailService;

    public void setAmazonSimpleEmailService(AmazonSimpleEmailService amazonSimpleEmailService) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
    }

    @Async
    @Override
    public void send(String to, String from, String subject, String content) {
        Destination destination = new Destination().withToAddresses(new String[]{to});
        Content textSubject = new Content().withData(subject);
        Content textContent = new Content().withData(content);

        // send text base email
        Message message = new Message().withSubject(textSubject).withBody(new Body().withText(textContent));
        SendEmailRequest request = new SendEmailRequest().withSource(from).withDestination(destination).withMessage(message);
        logger.debug("TO: " + to);
        logger.debug("FROM: " + from);
        logger.debug(subject);
        logger.debug(content);

        try {
            logger.debug("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");
            amazonSimpleEmailService.sendEmail(request);
            logger.debug("Email sent!");
        } catch (Exception e) {
            throw new SystemException("The email was not sent.", e);
        }
    }

}
