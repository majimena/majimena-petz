package org.majimena.petz.web.api.me;

import mockit.Injectable;
import mockit.Mocked;
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
import org.majimena.petz.domain.user.PasswordRegistry;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.UserService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see MyAccountController
 */
@RunWith(Enclosed.class)
public class MyAccountControllerTest {

    private static User newUser() {
        return User.builder()
                .id("1")
                .username("12345678901234567890123456789012345678901234567890")
                .login("test@example.com")
                .password("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .email("test@example.com")
                .activated(Boolean.TRUE)
                .build();
    }

    private static PasswordRegistry newPasswordRegistry() {
        return PasswordRegistry.builder()
                .userId("1")
                .newPassword("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .oldPassword("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;
        @Tested
        private MyAccountController sut = new MyAccountController();
        @Injectable
        private UserService userService;
        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void ログインユーザーのアカウントが取得できること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "1";
                userService.getUserByUserId("1");
                result = Optional.of(newUser());
            }};

            mockMvc.perform(get("/api/v1/me"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("1")))
                    .andExpect(jsonPath("$.username", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.login", is("test@example.com")))
                    .andExpect(jsonPath("$.password", is(nullValue())))
                    .andExpect(jsonPath("$.activated", is(true)))
                    .andExpect(jsonPath("$.email", is("test@example.com")));
        }

        @Test
        public void アカウントが取得できない場合は404になること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "1";
                userService.getUserByUserId("1");
                result = Optional.ofNullable(null);
            }};

            mockMvc.perform(get("/api/v1/me"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PutTest {

        private MockMvc mockMvc;
        @Tested
        private MyAccountController sut = new MyAccountController();
        @Injectable
        private UserService userService;
        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void アカウントが変更ができること() throws Exception {
            User data = newUser();

            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "1";
                userService.updateUser(data);
                result = data;
            }};

            mockMvc.perform(put("/api/v1/me")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("1")))
                    .andExpect(jsonPath("$.username", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.login", is("test@example.com")))
                    .andExpect(jsonPath("$.password", is(nullValue())))
                    .andExpect(jsonPath("$.activated", is(true)))
                    .andExpect(jsonPath("$.email", is("test@example.com")));
        }

        @Test
        public void ユーザ名にエラーがある場合は登録できないこと() throws Exception {
            User data = newUser();

            // 未入力
            data.setUsername(null);
            mockMvc.perform(put("/api/v1/me")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("username")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 文字数オーバー
            data.setUsername("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
            mockMvc.perform(put("/api/v1/me")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("username")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PutPasswordTest {

        private MockMvc mockMvc;
        @Tested
        private MyAccountController sut = new MyAccountController();
        @Injectable
        private UserService userService;
        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void パスワードが変更ができること() throws Exception {
            PasswordRegistry data = newPasswordRegistry();

            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "1";
                userService.changePassword(data);
                result = null;
            }};

            mockMvc.perform(put("/api/v1/me/password")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void パスワードにエラーがある場合は登録できないこと() throws Exception {
            PasswordRegistry data = newPasswordRegistry();

            data.setNewPassword(null);
            mockMvc.perform(put("/api/v1/me/password")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("newPassword")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            data.setNewPassword("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
            mockMvc.perform(put("/api/v1/me/password")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("newPassword")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 5 and 100")));
        }

        @Test
        public void 古いパスワードにエラーがある場合は登録できないこと() throws Exception {
            PasswordRegistry data = newPasswordRegistry();

            data.setOldPassword(null);
            mockMvc.perform(put("/api/v1/me/password")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("oldPassword")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            data.setOldPassword("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
            mockMvc.perform(put("/api/v1/me/password")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("oldPassword")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 5 and 100")));
        }
    }
}
