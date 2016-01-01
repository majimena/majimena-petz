package org.majimena.petz.service;

import org.majimena.petz.domain.Certificate;

import java.util.List;
import java.util.Optional;

/**
 * 証明書サービス.
 */
public interface CertificateService {

    /**
     * チケットIDをもとに証明書を取得する.
     *
     * @param ticketId チケットID
     * @return 証明書の一覧
     */
    List<Certificate> getCertificatesByTicketId(String ticketId);

    /**
     * 証明書IDをもとに証明書を取得する.
     *
     * @param certificateId 証明書ID
     * @return 証明書
     */
    Optional<Certificate> getCertificateByCertificateId(String certificateId);

    /**
     * 証明書を保存する.
     *
     * @param certificate 保存する証明書
     * @return 保存した証明書
     */
    Certificate saveCertificate(Certificate certificate);

    /**
     * 証明書を削除する.
     *
     * @param certificate 削除する証明書
     */
    void deleteCertificate(Certificate certificate);
}
