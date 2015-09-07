package org.majimena.petz.web.api.customer;

import org.majimena.petz.domain.customer.CustomerRegistry;
import org.majimena.petz.web.api.AbstractValidator;
import org.springframework.validation.Errors;

import javax.inject.Named;
import java.util.Optional;

/**
 * 顧客登録簿のバリデータ.
 */
@Named("customerRegistryValidator")
public class CustomerRegistryValidator extends AbstractValidator<CustomerRegistry> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<CustomerRegistry> target, Errors errors) {
    }
}
