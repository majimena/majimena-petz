package org.majimena.petz.web.api.clinic;

import junit.framework.TestCase;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.TestUtils;
import org.majimena.petz.domain.customer.CustomerAuthorizationToken;
import org.majimena.petz.service.CustomerService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by todoken on 2015/09/07.
 */
@RunWith(Enclosed.class)
public class ClinicUserControllerTest extends TestCase {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class AuthorizeTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicUserController sut;

        @Inject
        private WebApplicationContext webApplicationContext;

        @Mocked
        private CustomerService customerService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            sut.setCustomerService(customerService);
        }

        @Test
        public void 電話番号で認証ができること() throws Exception {
            final CustomerAuthorizationToken testdata = new CustomerAuthorizationToken("c12345", "u12345", "0311112222");
            new NonStrictExpectations() {{
                customerService.authorize(testdata);
                result = null;
            }};

            mockMvc.perform(post("/api/v1/clinics/c12345/users/u12345")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(testdata)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}