package org.majimena.petical.common.aws;

/**
 * AWSのSESを用いたラッパーサービス.
 */
public interface AmazonSESService {

    /**
     * メールを送信する.
     *
     * @param to      メール送信先アドレス
     * @param from    メール送信元アドレス
     * @param subject メール件名
     * @param content メール本文
     */
    void send(String to, String from, String subject, String content);

}
