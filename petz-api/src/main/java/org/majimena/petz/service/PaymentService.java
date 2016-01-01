package org.majimena.petz.service;

import org.majimena.petz.domain.Payment;

/**
 * ペイメントサービス.
 */
public interface PaymentService {

    /**
     * ペイメントを保存する.
     *
     * @param payment ペイメント
     * @return 保存したペイメント
     */
    Payment savePayment(Payment payment);
}
