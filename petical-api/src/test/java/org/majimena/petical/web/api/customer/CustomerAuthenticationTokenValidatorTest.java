package org.majimena.petical.web.api.customer;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.majimena.petical.domain.Customer;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.customer.CustomerAuthenticationToken;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.CustomerRepository;
import org.majimena.petical.repository.UserRepository;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see CustomerAuthenticationTokenValidator
 */
public class CustomerAuthenticationTokenValidatorTest {

    @Tested
    private CustomerAuthenticationTokenValidator sut = new CustomerAuthenticationTokenValidator();
    @Injectable
    private UserRepository userRepository;
    @Injectable
    private CustomerRepository customerRepository;

    private static CustomerAuthenticationToken newCustomerAuthenticationToken() {
        return CustomerAuthenticationToken.builder()
                .clinicId("1")
                .login("test@example.com")
                .lastName("12345678901234567890123456789012345678901234567890")
                .firstName("12345678901234567890123456789012345678901234567890")
                .phoneNo("123456789012345")
                .build();
    }

    private static User newUser() {
        return User.builder()
                .id("user1")
                .login("test@example.com")
                .lastName("12345678901234567890123456789012345678901234567890")
                .firstName("12345678901234567890123456789012345678901234567890")
                .phoneNo("123456789012345")
                .mobilePhoneNo("09012341234")
                .build();
    }

    @Test
    public void 対象が指定されていなくてもエラーにならないこと() throws Exception {
        CustomerAuthenticationToken data = newCustomerAuthenticationToken();
        Errors errors = new BindException(data, "customerAuthenticationToken");

        sut.validate(Optional.ofNullable(null), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 正常時はエラーにならないこと() throws Exception {
        CustomerAuthenticationToken data = newCustomerAuthenticationToken();
        Errors errors = new BindException(data, "customerAuthenticationToken");

        new NonStrictExpectations() {{
            userRepository.findOneByActivatedIsTrueAndLogin("test@example.com");
            result = Optional.of(newUser());
            customerRepository.findByClinicIdAndUserId("1", "user1");
            result = Optional.empty();
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void 姓が違う場合はエラーになること() throws Exception {
        CustomerAuthenticationToken data = newCustomerAuthenticationToken();
        data.setLastName("test");
        Errors errors = new BindException(data, "customerAuthenticationToken");

        new NonStrictExpectations() {{
            userRepository.findOneByActivatedIsTrueAndLogin("test@example.com");
            result = Optional.of(newUser());
            customerRepository.findByClinicIdAndUserId("1", "user1");
            result = Optional.empty();
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is(ErrorCode.PTZ_000201.name()));
    }

    @Test
    public void 名が違う場合はエラーになること() throws Exception {
        CustomerAuthenticationToken data = newCustomerAuthenticationToken();
        data.setFirstName("test");
        Errors errors = new BindException(data, "customerAuthenticationToken");

        new NonStrictExpectations() {{
            userRepository.findOneByActivatedIsTrueAndLogin("test@example.com");
            result = Optional.of(newUser());
            customerRepository.findByClinicIdAndUserId("1", "user1");
            result = Optional.empty();
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is(ErrorCode.PTZ_000201.name()));
    }

    @Test
    public void 電話番号が違う場合はエラーになること() throws Exception {
        CustomerAuthenticationToken data = newCustomerAuthenticationToken();
        data.setPhoneNo("0312341234");
        Errors errors = new BindException(data, "customerAuthenticationToken");

        new NonStrictExpectations() {{
            userRepository.findOneByActivatedIsTrueAndLogin("test@example.com");
            result = Optional.of(newUser());
            customerRepository.findByClinicIdAndUserId("1", "user1");
            result = Optional.empty();
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is(ErrorCode.PTZ_000201.name()));
    }

    @Test
    public void ユーザーが存在しない場合はエラーになること() throws Exception {
        CustomerAuthenticationToken data = newCustomerAuthenticationToken();
        Errors errors = new BindException(data, "customerAuthenticationToken");

        new NonStrictExpectations() {{
            userRepository.findOneByActivatedIsTrueAndLogin("test@example.com");
            result = Optional.empty();
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is(ErrorCode.PTZ_000999.name()));
    }

    @Test
    public void 既に顧客が存在する場合はエラーになること() throws Exception {
        CustomerAuthenticationToken data = newCustomerAuthenticationToken();
        Errors errors = new BindException(data, "customerAuthenticationToken");

        new NonStrictExpectations() {{
            userRepository.findOneByActivatedIsTrueAndLogin("test@example.com");
            result = Optional.of(newUser());
            customerRepository.findByClinicIdAndUserId("1", "user1");
            result = Optional.of(Customer.builder().id("customer1").build());
        }};

        sut.validate(Optional.of(data), errors);

        assertThat(errors, is(notNullValue()));
        assertThat(errors.hasErrors(), is(true));
        assertThat(errors.getAllErrors().size(), is(1));
        assertThat(errors.getAllErrors().get(0).getCode(), is(ErrorCode.PTZ_000101.name()));
    }
}
