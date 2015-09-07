package org.majimena.petz.web.api.user;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.TestUtils;
import org.majimena.petz.domain.user.UserCriteria;
import org.majimena.petz.domain.user.UserOutline;
import org.majimena.petz.service.UserService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserController
 */
@RunWith(Enclosed.class)
public class UserControllerTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;

        @Inject
        private UserController sut;

        @Inject
        private WebApplicationContext webApplicationContext;

        @Mocked
        private UserService userService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            sut.setUserService(userService);
        }

        @Test
        public void 検索条件に該当するユーザーが取得できること() throws Exception {
            new NonStrictExpectations() {{
                userService.getUsersByUserCriteria(new UserCriteria("test@example.com"));
                result = Arrays.asList(new UserOutline("u1", "Taro", "Test", Boolean.FALSE));
            }};

            mockMvc.perform(get("/api/v1/users")
                    .param("email", "test@example.com")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].id", is("u1")))
                    .andExpect(jsonPath("$.[0].firstName", is("Taro")))
                    .andExpect(jsonPath("$.[0].lastName", is("Test")));
        }
    }
}
