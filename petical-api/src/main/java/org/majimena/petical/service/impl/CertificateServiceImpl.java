package org.majimena.petical.service.impl;

import org.majimena.petical.domain.Certificate;
import org.majimena.petical.repository.CertificateRepository;
import org.majimena.petical.service.CertificateService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * 証明書サービスの実装.
 */
@Service
public class CertificateServiceImpl implements CertificateService {

    /**
     * 証明書リポジトリ.
     */
    @Inject
    private CertificateRepository certificateRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Certificate> getCertificatesByTicketId(String ticketId) {
        List<Certificate> list = certificateRepository.findByTicketId(ticketId);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Certificate> getCertificateByCertificateId(String certificateId) {
        Certificate one = certificateRepository.findOne(certificateId);
        return Optional.ofNullable(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Certificate saveCertificate(Certificate certificate) {
        Certificate save = certificateRepository.save(certificate);
        return save;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCertificate(Certificate certificate) {
        Certificate one = certificateRepository.findOne(certificate.getId());
        certificateRepository.delete(one);
    }
}
