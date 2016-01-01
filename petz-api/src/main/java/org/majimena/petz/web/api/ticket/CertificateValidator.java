package org.majimena.petz.web.api.ticket;

import org.majimena.petz.domain.Certificate;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.TicketRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * 証明書ドメインのカスタムバリデータ.
 */
@Named("certificateValidator")
public class CertificateValidator extends AbstractValidator<Certificate> {

    /**
     * クリニックリポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Certificate> target, Errors errors) {
        target.ifPresent(certificate -> {
            Ticket ticket = validateTicketId(certificate.getTicket().getId(), errors);
            Clinic clinic = validateClinicId(certificate.getTicket().getClinic().getId(), errors);

            ErrorsUtils.throwIfNotEqual(ticket.getClinic().getId(), clinic.getId());
            certificate.setTicket(ticket);
        });
    }

    private Ticket validateTicketId(String ticketId, Errors errors) {
        Ticket one = ticketRepository.findOne(ticketId);
        if (one == null) {
            ErrorsUtils.rejectValue("ticket", ErrorCode.PTZ_100999, errors);
        }
        return one;
    }

    private Clinic validateClinicId(String clinicId, Errors errors) {
        Clinic one = clinicRepository.findOne(clinicId);
        if (one == null) {
            ErrorsUtils.rejectValue("ticket.clinic", ErrorCode.PTZ_001999, errors);
        }
        return one;
    }
}
