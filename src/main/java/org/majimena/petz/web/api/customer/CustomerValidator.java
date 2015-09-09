package org.majimena.petz.web.api.customer;

import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.customer.CustomerRegistry;
import org.majimena.petz.web.api.AbstractValidator;
import org.springframework.validation.Errors;

import javax.inject.Named;
import java.util.Optional;

/**
 * 顧客バリデータ.
 */
@Named("customerValidator")
public class CustomerValidator extends AbstractValidator<Customer> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Customer> target, Errors errors) {
    }
}
