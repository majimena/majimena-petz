package org.majimena.petical.web.api.customer;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.majimena.petical.common.exceptions.ResourceNotFoundException;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.Customer;
import org.majimena.petical.domain.User;
import org.majimena.petical.repository.CustomerRepository;
import org.majimena.petical.repository.UserRepository;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see CustomerValidator
 */
public class CustomerValidatorTest {

    @Tested
    private CustomerValidator sut = new CustomerValidator();
    @Injectable
    private CustomerRepository customerRepository;
    @Injectable
    private UserRepository userRepository;

    private static Customer newCustomer() {
        return Customer.builder()
                .id("customer1")
                .clinic(Clinic.builder().id("1").build())
                .user(User.builder().id("user1").login("test@example.com").build())
                .blocked(Boolean.FALSE)
                .activated(Boolean.TRUE)
                .build();
    }

    @Test
    public void 対象が指定されていなくてもエラーにならないこと() throws Exception {
        Customer data = newCustomer();
        Errors errors = new BindException(data, "customer");

        sut.validate(Optional.ofNullable(null), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 正常時はエラーにならないこと() throws Exception {
        Customer data = newCustomer();
        Errors errors = new BindException(data, "customer");

        new NonStrictExpectations() {{
            customerRepository.findOne("customer1");
            result = Customer.builder().id("customer1").clinic(Clinic.builder().id("1").build()).build();
            userRepository.findOneByLogin("test@example.com");
            result = null;
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 新規登録の場合でもエラーにならないこと() throws Exception {
        Customer data = newCustomer();
        Errors errors = new BindException(data, "customer");

        new NonStrictExpectations() {{
            customerRepository.findOne("customer1");
            result = Customer.builder().id("customer1").clinic(Clinic.builder().id("1").build()).build();
        }};

        data.setId(null);
        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 更新時に処理対象IDが存在しない場合はエラーになること() throws Exception {
        Customer data = newCustomer();
        Errors errors = new BindException(data, "customer");

        new NonStrictExpectations() {{
            customerRepository.findOne("customer1");
            result = null;
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("PTZ_999998"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void 更新対象が別クリニックのデータである場合は例外になること() throws Exception {
        Customer data = newCustomer();
        Errors errors = new BindException(data, "customer");

        new NonStrictExpectations() {{
            customerRepository.findOne("customer1");
            result = Customer.builder().id("customer1").clinic(Clinic.builder().id("999").build()).build();
        }};

        sut.validate(Optional.of(data), errors);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void 新規登録時にログインIDが重複している場合は例外になること() throws Exception {
        Customer data = newCustomer();
        Errors errors = new BindException(data, "customer");

        new NonStrictExpectations() {{
            customerRepository.findOne("customer1");
            result = Customer.builder().id("customer1").user(User.builder().id("1").login("user@user.com").build()).clinic(Clinic.builder().id("999").build()).build();
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is("PTZ_000101"));
    }
}
