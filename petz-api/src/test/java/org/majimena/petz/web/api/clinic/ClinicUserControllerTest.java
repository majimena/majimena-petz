package org.majimena.petz.web.api.clinic;

import junit.framework.TestCase;
import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.TestUtils;
import org.majimena.petz.WebAppTestConfiguration;
import org.majimena.petz.config.SpringMvcConfiguration;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.customer.CustomerAuthenticationToken;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.CustomerService;
import org.majimena.petz.service.UserService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see ClinicUserController
 */
@RunWith(Enclosed.class)
public class ClinicUserControllerTest extends TestCase {

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
                .id("1")
                .login("test@example.com")
                .password("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .username("12345678901234567890123456789012345678901234567890")
                .lastName("12345678901234567890123456789012345678901234567890")
                .firstName("12345678901234567890123456789012345678901234567890")
                .phoneNo("123456789012345")
                .mobilePhoneNo("123456789012345")
                .activated(Boolean.TRUE)
                .build();
    }

    private static Customer newCustomer() {
        return Customer.builder()
                .id("customer1")
                .clinic(Clinic.builder().id("1").build())
                .user(newUser())
                .activated(Boolean.FALSE)
                .blocked(Boolean.FALSE)
                .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class AuthenticateTest {

        private MockMvc mockMvc;
        @Tested
        private ClinicUserController sut = new ClinicUserController();
        @Injectable
        private UserService userService;
        @Injectable
        private CustomerService customerService;
        @Injectable
        private CustomerAuthenticationTokenValidator customerAuthenticationTokenValidator;
        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void 権限がない場合はアクセスできないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException("");
            }};

            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                    .andExpect(jsonPath("$.title", is("Unauthorized")))
                    .andExpect(jsonPath("$.status", is(401)))
                    .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void 認証に成功するとユーザー情報が取得できること() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;
                customerAuthenticationTokenValidator.validate(data, (Errors) any);
                result = null;
                userService.getUserByLogin("test@example.com");
                result = Optional.of(newUser());
            }};

            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("1")))
                    .andExpect(jsonPath("$.login", is("test@example.com")))
                    .andExpect(jsonPath("$.password", is(nullValue())))
                    .andExpect(jsonPath("$.username", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.lastName", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.firstName", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.phoneNo", is("123456789012345")))
                    .andExpect(jsonPath("$.mobilePhoneNo", is("123456789012345")))
                    .andExpect(jsonPath("$.activated", is(Boolean.TRUE)));

            new Verifications() {{
                userService.getUserByLogin(anyString);
                times = 1;
            }};
        }

        @Test
        public void ログインにエラーがある場合は認証できないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();

            // 未入力
            data.setLogin("");
            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("login")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 型エラー
            data.setLogin("foo");
            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("login")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("foo")))
                    .andExpect(jsonPath("$.errors[0].message", is("not a well-formed email address")));
        }

        @Test
        public void 氏名の姓にエラーがある場合は認証できないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();

            // 未入力
            data.setLastName("");
            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("lastName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setLastName("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("lastName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 氏名の名にエラーがある場合は認証できないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();

            // 未入力
            data.setFirstName("");
            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("firstName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setFirstName("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("firstName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 電話番号にエラーがある場合は認証できないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();

            // 桁数オーバー
            data.setPhoneNo("1234567890123456");
            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("phoneNo")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("1234567890123456")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 15")));

            // 型エラー
            data.setPhoneNo("1234567890a");
            mockMvc.perform(post("/api/v1/clinics/1/users/authenticate")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("phoneNo")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("1234567890a")))
                    .andExpect(jsonPath("$.errors[0].message", is("must match \"^[0-9]+$\"")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class ImpTest {

        private MockMvc mockMvc;
        @Tested
        private ClinicUserController sut = new ClinicUserController();
        @Injectable
        private UserService userService;
        @Injectable
        private CustomerService customerService;
        @Injectable
        private CustomerAuthenticationTokenValidator customerAuthenticationTokenValidator;
        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void 権限がない場合はアクセスできないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException("");
            }};

            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                    .andExpect(jsonPath("$.title", is("Unauthorized")))
                    .andExpect(jsonPath("$.status", is(401)))
                    .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void 認証に成功するとユーザー情報が取得できること() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;
                customerAuthenticationTokenValidator.validate(data, (Errors) any);
                result = null;
                customerService.saveCustomer(data);
                result = newCustomer();
            }};

            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("customer1")))
                    .andExpect(jsonPath("$.clinic.id", is("1")))
                    .andExpect(jsonPath("$.user.id", is("1")))
                    .andExpect(jsonPath("$.activated", is(Boolean.FALSE)));

            new Verifications() {{
                customerService.saveCustomer(data);
                times = 1;
            }};
        }

        @Test
        public void ログインにエラーがある場合は認証できないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();

            // 未入力
            data.setLogin("");
            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("login")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 型エラー
            data.setLogin("foo");
            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("login")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("foo")))
                    .andExpect(jsonPath("$.errors[0].message", is("not a well-formed email address")));
        }

        @Test
        public void 氏名の姓にエラーがある場合は認証できないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();

            // 未入力
            data.setLastName("");
            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("lastName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setLastName("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("lastName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 氏名の名にエラーがある場合は認証できないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();

            // 未入力
            data.setFirstName("");
            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("firstName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setFirstName("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("firstName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 電話番号にエラーがある場合は認証できないこと() throws Exception {
            CustomerAuthenticationToken data = newCustomerAuthenticationToken();

            // 桁数オーバー
            data.setPhoneNo("1234567890123456");
            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("phoneNo")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("1234567890123456")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 15")));

            // 型エラー
            data.setPhoneNo("1234567890a");
            mockMvc.perform(post("/api/v1/clinics/1/users/import")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("phoneNo")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("1234567890a")))
                    .andExpect(jsonPath("$.errors[0].message", is("must match \"^[0-9]+$\"")));
        }
    }
}
