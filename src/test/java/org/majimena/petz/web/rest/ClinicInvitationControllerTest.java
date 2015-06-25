package org.majimena.petz.web.rest;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.TestUtils;
import org.majimena.petz.service.ClinicService;
import org.majimena.petz.web.rest.dto.ClinicInvitationDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see ClinicInvitationController
 */
@RunWith(Enclosed.class)
public class ClinicInvitationControllerTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class PostTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicInvitationController sut;

        @Inject
        private WebApplicationContext wac;

        @Mocked
        private ClinicService clinicService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
            sut.setClinicService(clinicService);
        }

        @Test
        public void 招待状を送信できること() throws Exception {
            ClinicInvitationDTO testData = new ClinicInvitationDTO(new String[]{"foo@localhost.com", "bar@localhost.com"});

            new NonStrictExpectations() {{
                clinicService.inviteStaff(100L, new HashSet<>(Arrays.asList("foo@localhost.com", "bar@localhost.com")));
            }};

            mockMvc.perform(post("/v1/clinics/100/invitations")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isCreated());
        }

        @Test
        public void メールアドレスが入力されていない場合はエラーになること() throws Exception {
            ClinicInvitationDTO testData = new ClinicInvitationDTO();

            mockMvc.perform(post("/v1/clinics/100/invitations")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.type", is(TestUtils.Type.TYPE_400)))
                .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("emails")))
                .andExpect(jsonPath("$.errors[0].message", is(TestUtils.Message.NULL)));
        }

        @Test
        public void メールアドレスのサイズがオーバーしている場合はエラーになること() throws Exception {
            ClinicInvitationDTO testData = new ClinicInvitationDTO(new String[101]);

            mockMvc.perform(post("/v1/clinics/100/invitations")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.type", is(TestUtils.Type.TYPE_400)))
                .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("emails")))
                .andExpect(jsonPath("$.errors[0].message", is("size must be between 1 and 100")));
        }

        @Test
        public void メールアドレスのサイズが足りない場合はエラーになること() throws Exception {
            ClinicInvitationDTO testData = new ClinicInvitationDTO(new String[0]);

            mockMvc.perform(post("/v1/clinics/100/invitations")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.type", is(TestUtils.Type.TYPE_400)))
                .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("emails")))
                .andExpect(jsonPath("$.errors[0].message", is("size must be between 1 and 100")));
        }

        @Test
        public void メールアドレスではない場合はエラーになること() throws Exception {
            ClinicInvitationDTO testData = new ClinicInvitationDTO(new String[]{"1234567890"});

            mockMvc.perform(post("/v1/clinics/100/invitations")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.type", is(TestUtils.Type.TYPE_400)))
                .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("emails")))
                .andExpect(jsonPath("$.errors[0].message", is(TestUtils.Message.EMAIL)));
        }
    }
}
