package org.majimena.petz.web.api.clinic;

import com.google.common.collect.Sets;
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
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicInvitationAcception;
import org.majimena.petz.domain.clinic.ClinicInvitationRegistry;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicInvitationService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see ClinicInvitationController
 */
@RunWith(Enclosed.class)
public class ClinicInvitationControllerTest {

    protected static ClinicInvitationRegistry newClinicInvitationRegistry() {
        return ClinicInvitationRegistry.builder()
                .emails(new String[]{"foo@localhost.com", "bar@localhost.com"})
                .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class InviteTest {

        private MockMvc mockMvc;

        @Tested
        private ClinicInvitationController sut = new ClinicInvitationController();

        @Injectable
        private ClinicInvitationRegistryValidator clinicInvitationRegistryValidator;

        @Injectable
        private ClinicInvitationService clinicInvitationService;

        @Injectable
        private ClinicInvitationAcceptionValidator clinicInvitationAcceptionValidator;

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
            ClinicInvitationRegistry data = newClinicInvitationRegistry();
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException("");
            }};

            mockMvc.perform(post("/api/v1/clinics/1/invitations")
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
        public void 招待状を送信できること() throws Exception {
            ClinicInvitationRegistry data = newClinicInvitationRegistry();
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;
                clinicInvitationService.inviteStaff("1", "1", Sets.newHashSet("foo@localhost.com", "bar@localhost.com"));
                result = null;
            }};

            mockMvc.perform(post("/api/v1/clinics/1/invitations")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        @Test
        public void メールアドレスにエラーがある場合は招待できないこと() throws Exception {
            ClinicInvitationRegistry data = new ClinicInvitationRegistry();

            // 未入力
            data.setEmails(null);
            mockMvc.perform(post("/api/v1/clinics/1/invitations")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is("emails")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // サイズオーバー
            data.setEmails(new String[21]);
            mockMvc.perform(post("/api/v1/clinics/1/invitations")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is("emails")))
                    //                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 1 and 20")));

            // サイズ不足
            data.setEmails(new String[0]);
            mockMvc.perform(post("/api/v1/clinics/1/invitations")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is("emails")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 1 and 20")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class ShowTest {

        private MockMvc mockMvc;

        @Tested
        private ClinicInvitationController sut = new ClinicInvitationController();

        @Injectable
        private ClinicInvitationRegistryValidator clinicInvitationRegistryValidator;

        @Injectable
        private ClinicInvitationService clinicInvitationService;

        @Injectable
        private ClinicInvitationAcceptionValidator clinicInvitationAcceptionValidator;

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
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("100");
                result = new ResourceCannotAccessException("");
            }};

            mockMvc.perform(get("/api/v1/clinics/100/invitations/1"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                    .andExpect(jsonPath("$.title", is("Unauthorized")))
                    .andExpect(jsonPath("$.status", is(401)))
                    .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void 招待状が取得できること() throws Exception {
            new NonStrictExpectations() {{
                clinicInvitationService.getClinicInvitationById("1");
                result = new ClinicInvitation("1", new Clinic(), new User(), new User(), "foo@localhost", "foo");
            }};

            mockMvc.perform(get("/api/v1/clinics/100/invitations/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("1")))
                    .andExpect(jsonPath("$.clinic", is(notNullValue())))
                    .andExpect(jsonPath("$.user", is(notNullValue())))
                    .andExpect(jsonPath("$.email", is("foo@localhost")))
            ;
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class ActivateTest {

        private MockMvc mockMvc;

        @Tested
        private ClinicInvitationController sut = new ClinicInvitationController();

        @Injectable
        private ClinicInvitationRegistryValidator clinicInvitationRegistryValidator;

        @Injectable
        private ClinicInvitationService clinicInvitationService;

        @Injectable
        private ClinicInvitationAcceptionValidator clinicInvitationAcceptionValidator;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        private ClinicInvitationAcception newClinicInvitationAcception() {
            return ClinicInvitationAcception.builder()
                    .clinicId("")
                    .clinicInvitationId("")
                    .activationKey("1234567890")
                    .build();
        }

        @Test
        public void 権限がない場合はアクセスできないこと() throws Exception {
            ClinicInvitationAcception data = newClinicInvitationAcception();
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException("");
            }};

            mockMvc.perform(put("/api/v1/clinics/1/invitations/1")
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
        public void 招待をアクティベートできること() throws Exception {
            ClinicInvitationAcception data = newClinicInvitationAcception();
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;
                clinicInvitationAcceptionValidator.validate(data, (Errors) any);
                result = null;
                clinicInvitationService.activate("1", "123456789012345678901234567890123456789012345678901234567890");
                result = null;
            }};

            mockMvc.perform(put("/api/v1/clinics/1/invitations/1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception(null, null, "123456789012345678901234567890123456789012345678901234567890"))))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void アクティベーションキーにエラーがある場合はアクティベートできないこと() throws Exception {
            ClinicInvitationAcception data = new ClinicInvitationAcception();

            // 未入力
            data.setActivationKey("");
            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is("activationKey")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 文字数オーバー
            data.setActivationKey("1234567890123456789012345678901234567890123456789012345678901");
            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is("activationKey")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("1234567890123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 60")));
        }
    }
}
