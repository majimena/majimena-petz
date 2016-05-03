package org.majimena.petical.web.api.ticket;

import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.CustomerRepository;
import org.majimena.petical.repository.PetRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.web.api.AbstractValidator;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * チケットドメインのカスタムバリデータ.
 */
@Named("ticketValidator")
public class TicketValidator extends AbstractValidator<Ticket> {

    /**
     * クリニックリポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * ペットリポジトリ.
     */
    @Inject
    private PetRepository petRepository;

    /**
     * 顧客リポジトリ.
     */
    @Inject
    private CustomerRepository customerRepository;

    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Ticket> target, Errors errors) {
        target.ifPresent(ticket -> {
            // IDの存在チェック
            validateTicketId(Optional.ofNullable(ticket.getId()), ticket.getClinic().getId(), errors);

            // クリニックの存在チェック
            Clinic clinic = validateClinicId(ticket.getClinic().getId(), errors);
            ticket.setClinic(clinic);
        });
    }

    private void validateTicketId(Optional<String> value, String clinicId, Errors errors) {
        value.ifPresent(id -> {
            Ticket one = ticketRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.reject(ErrorCode.PTZ_999998, errors);
            } else {
                ErrorsUtils.throwIfNotEqual(clinicId, one.getClinic().getId());
            }
        });
    }

    private Clinic validateClinicId(String clinicId, Errors errors) {
        Clinic one = clinicRepository.findOne(clinicId);
        if (one == null) {
            ErrorsUtils.rejectValue("clinic", ErrorCode.PTZ_001999, errors);
        }
        return one;
    }
}
