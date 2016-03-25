package org.majimena.petz.web.api.user;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.TestUtils;
import org.majimena.petz.WebAppTestConfiguration;
import org.majimena.petz.config.SpringMvcConfiguration;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.service.UserService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see SignupController
 */
@RunWith(Enclosed.class)
public class SignupControllerTest {

    private static SignupRegistry newSignupRegistry() {
        return SignupRegistry.builder()
                .firstName("12345678901234567890123456789012345678901234567890")
                .lastName("12345678901234567890123456789012345678901234567890")
                .email("ken.todoroki123@abcdefghij.com")
                .password("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PostTest {

        private MockMvc mockMvc;
        @Tested
        private SignupController sut = new SignupController();
        @Injectable
        private UserService userService;
        @Injectable
        private SignupRegistryValidator signupRegistryValidator;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void ユーザが登録できること() throws Exception {
            SignupRegistry data = newSignupRegistry();
            Errors errors = new BindException(data, "user");

            new NonStrictExpectations() {{
                signupRegistryValidator.validate(data, errors);
                result = null;
                userService.saveUser(data);
                result = User.builder()
                        .id("1")
                        .username(data.getFirstName())
                        .firstName(data.getFirstName())
                        .lastName(data.getLastName())
                        .login(data.getEmail())
                        .email(data.getEmail())
                        .password(data.getPassword())
                        .build();
            }};

            mockMvc.perform(post("/api/v1/signup")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is("1")))
                    .andExpect(jsonPath("$.username", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.firstName", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.lastName", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.email", is("ken.todoroki123@abcdefghij.com")))
                    .andExpect(jsonPath("$.login", is("ken.todoroki123@abcdefghij.com")))
                    .andExpect(jsonPath("$.password", is(nullValue())));
        }

        @Test
        public void 氏名姓にエラーがある場合は登録できないこと() throws Exception {
            SignupRegistry data = newSignupRegistry();

            // 未入力
            data.setLastName(null);
            mockMvc.perform(post("/api/v1/signup")
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
            mockMvc.perform(post("/api/v1/signup")
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
        public void 氏名名にエラーがある場合は登録できないこと() throws Exception {
            SignupRegistry data = newSignupRegistry();

            // 未入力
            data.setFirstName(null);
            mockMvc.perform(post("/api/v1/signup")
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
            mockMvc.perform(post("/api/v1/signup")
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
        public void メールアドレスにエラーがある場合は登録できないこと() throws Exception {
            SignupRegistry data = newSignupRegistry();

            // 未入力
            data.setEmail(null);
            mockMvc.perform(post("/api/v1/signup")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("email")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // メールアドレスではない
            data.setEmail("abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij12345@abcdefghij.com");
            mockMvc.perform(post("/api/v1/signup")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("email")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij12345@abcdefghij.com")))
                    .andExpect(jsonPath("$.errors[0].message", is("not a well-formed email address")));
        }

        @Test
        public void パスワードにエラーがある場合は登録できないこと() throws Exception {
            SignupRegistry data = newSignupRegistry();

            // 未入力
            data.setPassword(null);
            mockMvc.perform(post("/api/v1/signup")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("password")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setPassword("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/signup")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("password")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 5 and 100")));

            // 桁数不足
            data.setPassword("1234");
            mockMvc.perform(post("/api/v1/signup")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("password")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("1234")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 5 and 100")));
        }
    }
}
