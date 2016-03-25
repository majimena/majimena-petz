package org.majimena.petz.web.api.invoice;

import org.majimena.petz.domain.Invoice;
import org.majimena.petz.domain.Payment;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.InvoiceRepository;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import java.util.Optional;

/**
 * ペイメントのカスタムバリデータ.
 */
@Component
@Transactional(readOnly = true)
public class PaymentValidator extends AbstractValidator<Payment> {

    /**
     * インヴォイスリポジトリ.
     */
    @Inject
    private InvoiceRepository invoiceRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Payment> target, Errors errors) {
        target.ifPresent(payment -> {
            // 請求情報のバリデーション
            Invoice invoice = validateInvoice(Optional.ofNullable(payment.getInvoice()), errors);
            payment.setInvoice(invoice);
        });
    }

    private Invoice validateInvoice(Optional<Invoice> value, Errors errors) {
        return value.map(invoice -> {
            // 請求情報の存在確認
            Invoice one = invoiceRepository.findOne(invoice.getId());
            if (one == null) {
                ErrorsUtils.rejectValue("invoice", ErrorCode.PTZ_100999, errors);
                return null;
            }

            // チケットの存在確認
            Ticket ticket = one.getTicket();
            if (ticket == null) {
                ErrorsUtils.rejectValue("ticket", ErrorCode.PTZ_100999, errors);
                return null;
            }

            // クリニック権限のチェック
            SecurityUtils.throwIfDoNotHaveClinicRoles(ticket.getClinic().getId());
            return one;
        }).orElse(null);
    }
}
