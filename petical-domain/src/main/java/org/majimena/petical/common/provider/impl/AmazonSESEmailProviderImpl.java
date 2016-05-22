package org.majimena.petical.common.provider.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.common.provider.EmailProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

/**
 * メールマネジャーの実装クラス.
 */
public class AmazonSESEmailProviderImpl implements EmailProvider {

    /**
     * ログ.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSESEmailProviderImpl.class);

    /**
     * テンプレートエンジン.
     */
    private SpringTemplateEngine templateEngine;

    /**
     * SESサービスクライアント.
     */
    private AmazonSimpleEmailService amazonSimpleEmailService;

    /**
     * 送信元名称.
     */
    private String name;

    /**
     * 送信元メールアドレス.
     */
    private String from;

    /**
     * 文字コード.
     */
    private String charset;

    /**
     * テンプレートエンジンを設定します.
     *
     * @param templateEngine テンプレートエンジン
     */
    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * AWSのSESサービスクライアントを設定します.
     *
     * @param amazonSimpleEmailService AWSのSESサービスクライアント
     */
    public void setAmazonSimpleEmailService(AmazonSimpleEmailService amazonSimpleEmailService) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
    }

    /**
     * 送信元名称を設定します.
     *
     * @param name 送信元名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 送信元メールアドレスを設定します.
     *
     * @param from 送信元メールアドレス
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * 文字コードを設定します.
     *
     * @param charset 文字コード
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        this.sendEmail(to, null, subject, template, variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmail(String to, String bcc, String subject, String template, Map<String, Object> variables) {
        // テンプレートからメール文章を生成
        Context context = new Context(Locale.JAPANESE, variables);
        String data = templateEngine.process(template, context);

        // 送信メッセージを作成
        Destination destination = new Destination()
                .withToAddresses(to);
        Message message = new Message()
                .withSubject(new Content().withData(subject))
                .withBody(new Body().withHtml(new Content().withData(data)));
        SendEmailRequest request = new SendEmailRequest()
                .withSource(source())
                .withDestination(destination)
                .withMessage(message);

        // BCC
        if (StringUtils.isNotEmpty(bcc)) {
            destination.withBccAddresses(bcc);
        }

        // 非同期でメールを送信する
        LOGGER.trace(request.toString());
        amazonSimpleEmailService.sendEmail(request);
    }

    protected String source() {
        try {
            return new InternetAddress(from, name, charset)
                    .toString();
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("cannot convert mail address name.", e);
            return from;
        }
    }
}
