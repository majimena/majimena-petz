package org.majimena.petical.manager;

import java.util.Map;

/**
 * メールマネジャー.
 */
public interface MailManager {

    /**
     * メールを送信する.
     *
     * @param to        メール送信先アドレス
     * @param subject   メールタイトルのテンプレート名
     * @param content   メール本文のテンプレート名
     * @param variables メールのパラメータ
     */
    void sendEmail(String to, String subject, String content, Map<String, Object> variables);
}
