package org.majimena.petical.manager.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.majimena.petical.manager.MailManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Map;

/**
 * メールマネジャーの実装クラス.
 */
@Component
public class MailManagerImpl implements MailManager {

    /**
     * テンプレートエンジン.
     */
    @Inject
    private SpringTemplateEngine templateEngine;

    /**
     * SESサービスクライアント.
     */
    @Inject
    private AmazonSimpleEmailServiceAsync amazonSimpleEmailServiceAsync;

    /**
     * 送信元メールアドレス.
     */
    @Value("${mail.from:noreply@petz.io}")
    private String from;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        // テンプレートからメール文章を生成
        Context context = new Context(Locale.JAPANESE, variables);
        String cdata = templateEngine.process(template, context);

        // 送信メッセージを作成
        Destination destination = new Destination().withToAddresses(new String[]{to});
        Message message = new Message()
                .withSubject(new Content().withData(subject))
                .withBody(new Body().withHtml(new Content().withData(cdata)));
        SendEmailRequest request = new SendEmailRequest()
                .withSource(from)
                .withDestination(destination)
                .withMessage(message);

        // 非同期でメールを送信する
        amazonSimpleEmailServiceAsync.sendEmailAsync(request);
    }
}
