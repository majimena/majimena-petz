package org.majimena.petical.common.provider;

import java.util.Map;

/**
 * メールプロバイダ.
 */
public interface EmailProvider {

    /**
     * メールを送信する.
     *
     * @param to        メール送信先アドレス
     * @param subject   メールタイトルのテンプレート名
     * @param content   メール本文のテンプレート名
     * @param variables メールのパラメータ
     */
    void sendEmail(String to, String subject, String content, Map<String, Object> variables);

    /**
     * メールを送信する.
     *
     * @param to        メール送信先アドレス
     * @param bcc       BCC指定するメール送信先アドレス
     * @param subject   メールタイトルのテンプレート名
     * @param content   メール本文のテンプレート名
     * @param variables メールのパラメータ
     */
    void sendEmail(String to, String bcc, String subject, String content, Map<String, Object> variables);
}
