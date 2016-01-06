package org.majimena.petz.web.api.product;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ProductRepository;
import org.majimena.petz.web.api.AbstractValidator;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * プロダクトドメインのカスタムバリデータ.
 */
@Named("productValidator")
public class ProductValidator extends AbstractValidator<Product> {

    /**
     * プロダクトリポジトリ.
     */
    @Inject
    private ProductRepository productRepository;

    /**
     * クリニックリポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Product> target, Errors errors) {
        target.ifPresent(product -> {
            // IDの存在チェック
            validateProductId(Optional.ofNullable(product.getId()), product.getClinic().getId(), errors);

            // クリニックの存在チェック
            Clinic clinic = validateClinicId(product.getClinic().getId(), errors);
            product.setClinic(clinic);
        });
    }

    private void validateProductId(Optional<String> value, String clinicId, Errors errors) {
        value.ifPresent(id -> {
            Product one = productRepository.findOne(id);
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
