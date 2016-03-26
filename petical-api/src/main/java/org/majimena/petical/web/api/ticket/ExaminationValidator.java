package org.majimena.petical.web.api.ticket;

import org.majimena.petical.domain.Examination;
import org.majimena.petical.domain.Product;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.ExaminationRepository;
import org.majimena.petical.repository.ProductRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.web.api.AbstractValidator;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * 診察ドメインのカスタムバリデータ.
 */
@Named("examinationValidator")
public class ExaminationValidator extends AbstractValidator<Examination> {

    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * プロダクトリポジトリ.
     */
    @Inject
    private ProductRepository productRepository;

    /**
     * 診察リポジトリ.
     */
    @Inject
    private ExaminationRepository examinationRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    protected void validate(Optional<Examination> target, Errors errors) {
        target.ifPresent(examination -> {
            Ticket ticket = validateTicket(Optional.ofNullable(examination.getTicket()), errors);
            examination.setTicket(ticket);

            Product product = validateProduct(Optional.ofNullable(examination.getProduct()), errors);
            examination.setProduct(product);

            validateExaminationId(Optional.ofNullable(examination.getId()), errors);
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

    private Product validateProduct(Optional<Product> value, Errors errors) {
        return value.map(product -> {
            // 診察内容の存在確認
            Product one = productRepository.findOne(product.getId());
            if (one == null) {
                ErrorsUtils.rejectValue("product", ErrorCode.PTZ_005999, errors);
                return null;
            }

            // クリニック権限のチェック
            SecurityUtils.throwIfDoNotHaveClinicRoles(one.getClinic().getId());
            return one;
        }).orElse(null);
    }

    private void validateExaminationId(Optional<String> value, Errors errors) {
        value.ifPresent(id -> {
            // IDが指定されている場合、該当データの存在チェック
            Examination one = examinationRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.rejectValue("id", ErrorCode.PTZ_999998, errors);
            } else {
                // クリニック権限のチェック
                SecurityUtils.throwIfDoNotHaveClinicRoles(one.getTicket().getClinic().getId());
            }
        });
    }
}
