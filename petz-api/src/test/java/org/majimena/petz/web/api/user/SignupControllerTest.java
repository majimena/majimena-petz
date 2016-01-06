package org.majimena.petz.web.api.user;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.WebAppTestConfiguration;
import org.majimena.petz.config.SpringMvcConfiguration;
import org.majimena.petz.domain.User;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.UserService;
import org.majimena.petz.web.api.me.MyAccountController;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see SignupController
 */
@RunWith(Enclosed.class)
public class SignupControllerTest {

    private static User newUser() {
        return User.builder()
            .id("1")
            .username("")
            .login("test@example.com")
            .password("password")
            .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PostTest {

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
                result = "taro";
                userService.getUserByUserId("taro");
                result = Optional.of(newUser());
            }};

            mockMvc.perform(get("/api/v1/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Jackson")))
                .andExpect(jsonPath("$.login", is("test@example.com")))
                .andExpect(jsonPath("$.password", is(nullValue())))
                .andExpect(jsonPath("$.activated", is(true)))
                .andExpect(jsonPath("$.email", is("test@mail.example.com")));
        }

        @Test
        public void アカウントが取得できない場合は404になること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "taro";
                userService.getUserByUserId("taro");
                result = Optional.ofNullable(null);
            }};

            mockMvc.perform(get("/api/v1/me"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(is("")));
        }
    }
}
