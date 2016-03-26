package org.majimena.petical.web.api.ticket;

import org.majimena.petical.domain.Ticket;
import org.majimena.petical.domain.TicketAttachment;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.TicketAttachmentRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.web.api.AbstractValidator;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * チケット添付ファイルドメインのカスタムバリデータ.
 */
@Named("ticketAttachmentValidator")
public class TicketAttachmentValidator extends AbstractValidator<TicketAttachment> {

    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * チケット添付ファイルリポジトリ.
     */
    @Inject
    private TicketAttachmentRepository ticketAttachmentRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<TicketAttachment> target, Errors errors) {
        target.ifPresent(examination -> {
            Ticket ticket = validateTicket(Optional.ofNullable(examination.getTicket()), errors);
            examination.setTicket(ticket);

            validateTicketAttachmentId(Optional.ofNullable(examination.getId()), errors);
        });
    }

    private Ticket validateTicket(Optional<Ticket> value, Errors errors) {
        return value.map(ticket -> {
            // チケットの存在確認
            Ticket one = ticketRepository.findOne(ticket.getId());
            if (one == null) {
                ErrorsUtils.rejectValue("ticket", ErrorCode.PTZ_100999, errors);
                return null;
            }

            // クリニック権限のチェック
            SecurityUtils.throwIfDoNotHaveClinicRoles(one.getClinic().getId());
            return one;
        }).orElse(null);
    }

    private void validateTicketAttachmentId(Optional<String> value, Errors errors) {
        value.ifPresent(id -> {
            // IDが指定されている場合、該当データの存在チェック
            TicketAttachment one = ticketAttachmentRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("id", ErrorCode.PTZ_999998, errors);
            } else {
                // クリニック権限のチェック
                SecurityUtils.throwIfDoNotHaveClinicRoles(one.getTicket().getClinic().getId());
            }
        });
    }
}
