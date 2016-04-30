package org.majimena.petical.service.impl;

import org.majimena.petical.datatype.CertificateType;
import org.majimena.petical.datetime.L10nDateTimeProvider;
import org.majimena.petical.domain.Certificate;
import org.majimena.petical.domain.Chart;
import org.majimena.petical.repository.CertificateRepository;
import org.majimena.petical.repository.ChartRepository;
import org.majimena.petical.service.CertificateService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
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
     * カルテリポジトリ.
     */
    @Inject
    private ChartRepository chartRepository;

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
        LocalDateTime now = L10nDateTimeProvider.now().toLocalDateTime();

        // 狂犬病予防接種の場合
        if (certificate.getType() == CertificateType.RABID) {
            Chart chart = certificate.getTicket().getChart();
            Chart one = chartRepository.getOne(chart.getId());
            one.setRabidVaccineDate(now.plusYears(1L));
        }

        // 混合ワクチンの場合
        if (certificate.getType() == CertificateType.PREVENTION) {
            Chart chart = certificate.getTicket().getChart();
            Chart one = chartRepository.getOne(chart.getId());
            one.setMixVaccineName(certificate.getVaccine().getName());
            one.setMixVaccineDate(now.plusYears(1L));
        }

        // 証明書を保存する
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
