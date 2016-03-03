package org.majimena.petz.service;

import org.majimena.petz.domain.User;
import org.springframework.stereotype.Service;

/**
 * Service for sending e-mails.
 * <p/>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Deprecated
@Service
public interface MailService {

    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    void sendActivationEmail(User user, String baseUrl);

    void sendPasswordResetMail(User user, String baseUrl);

}
