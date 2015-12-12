package org.majimena.petz.web.api.ticket;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.CustomerRepository;
import org.majimena.petz.repository.PetRepository;
import org.majimena.petz.repository.TicketRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
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
            validateClinicId(ticket, errors);
            validatePetId(ticket, errors);
            validateCustomerId(ticket, errors);
            validateTicketId(ticket, errors);
        });
    }

    private void validateClinicId(Ticket ticket, Errors errors) {
        if (ticket.getClinic() != null) {
            String id = ticket.getClinic().getId();
            Clinic one = clinicRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("clinic", ErrorCode.PTZ_001999, errors);
            }
        }
    }

    private void validatePetId(Ticket ticket, Errors errors) {
        if (ticket.getPet() != null) {
            String id = ticket.getPet().getId();
            Pet one = petRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("pet", ErrorCode.PTZ_002999, errors);
            }
        }
    }

    private void validateCustomerId(Ticket ticket, Errors errors) {
        if (ticket.getCustomer() != null) {
            String id = ticket.getCustomer().getId();
            Customer one = customerRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("customer", ErrorCode.PTZ_003999, errors);
            }
        }
    }

    private void validateTicketId(Ticket ticket, Errors errors) {
        if (StringUtils.isNotEmpty(ticket.getId())) {
            Ticket one = ticketRepository.findOne(ticket.getId());
            if (one == null) {
                ErrorsUtils.reject(ErrorCode.PTZ_999998, errors);
            } else {
                if (!StringUtils.equals(one.getClinic().getId(), ticket.getClinic().getId())) {
                    ErrorsUtils.reject(ErrorCode.PTZ_999997, errors);
                }
            }
        }
    }
}
