package org.majimena.petical.service;

import org.majimena.petical.domain.Payment;

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
