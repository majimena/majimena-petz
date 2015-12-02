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
import org.majimena.petz.Application;
import org.majimena.petz.TestUtils;
import org.majimena.petz.WebAppTestConfiguration;
import org.majimena.petz.config.SpringMvcConfiguration;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicInvitationAcception;
import org.majimena.petz.domain.clinic.ClinicInvitationRegistry;
import org.majimena.petz.repository.ClinicInvitationRepository;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicInvitationService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

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
                .clinicId("")
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
                clinicInvitationService.inviteStaff("1", Sets.newHashSet("foo@localhost.com", "bar@localhost.com"));
                result = null;
            }};

            mockMvc.perform(post("/api/v1/clinics/1/invitations")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

//        @Test
//        public void クリニックが存在しない場合は404になること() throws Exception {
//            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[]{"foo@localhost.com"});
//
//            new NonStrictExpectations() {{
//                clinicRepository.findOne("100");
//                result = null;
//            }};
//
//            mockMvc.perform(post("/api/v1/clinics/100/invitations")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(testData)))
//                    .andDo(print())
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.status", is(404)));
//        }
//
//        @Test
//        public void 既にクリニックスタッフが存在している場合はエラーになること() throws Exception {
//            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[]{"foo@localhost.com"});
//
//            new NonStrictExpectations() {{
//                clinicRepository.findOne("100");
//                result = new Clinic();
//                userRepository.findOneByLogin("foo@localhost.com");
//                result = Optional.ofNullable(User.builder().id("1").build());
//                clinicStaffRepository.findByClinicIdAndUserId("100", "1");
//                result = Optional.ofNullable(new ClinicStaff());
//            }};
//
//            mockMvc.perform(post("/api/v1/clinics/100/invitations")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(testData)))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status", is(400)))
//                    .andExpect(jsonPath("$.type", is(TestUtils.Type.TYPE_400)))
//                    .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.errors", hasSize(1)))
//                    .andExpect(jsonPath("$.errors[0].field", is("emails")))
//                    .andExpect(jsonPath("$.errors[0].message", is(notNullValue())));
//        }
//
//        @Test
//        public void メールアドレスが入力されていない場合はエラーになること() throws Exception {
//            ClinicInvitationRegistry testData = new ClinicInvitationRegistry();
//
//            mockMvc.perform(post("/api/v1/clinics/100/invitations")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(testData)))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status", is(400)))
//                    .andExpect(jsonPath("$.type", is(TestUtils.Type.TYPE_400)))
//                    .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.errors", hasSize(1)))
//                    .andExpect(jsonPath("$.errors[0].field", is("emails")))
//                    .andExpect(jsonPath("$.errors[0].message", is(TestUtils.Message.NULL)));
//        }
//
//        @Test
//        public void メールアドレスのサイズがオーバーしている場合はエラーになること() throws Exception {
//            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[101]);
//
//            mockMvc.perform(post("/api/v1/clinics/100/invitations")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(testData)))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status", is(400)))
//                    .andExpect(jsonPath("$.type", is(TestUtils.Type.TYPE_400)))
//                    .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.errors", hasSize(1)))
//                    .andExpect(jsonPath("$.errors[0].field", is("emails")))
//                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 1 and 100")));
//        }
//
//        @Test
//        public void メールアドレスのサイズが足りない場合はエラーになること() throws Exception {
//            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[0]);
//
//            mockMvc.perform(post("/api/v1/clinics/100/invitations")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(testData)))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status", is(400)))
//                    .andExpect(jsonPath("$.type", is(TestUtils.Type.TYPE_400)))
//                    .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.errors", hasSize(1)))
//                    .andExpect(jsonPath("$.errors[0].field", is("emails")))
//                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 1 and 100")));
//        }
//
//        @Test
//        public void メールアドレスではない場合はエラーになること() throws Exception {
//            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[]{"1234567890"});
//
//            mockMvc.perform(post("/api/v1/clinics/100/invitations")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(testData)))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status", is(400)))
//                    .andExpect(jsonPath("$.type", is(TestUtils.Type.TYPE_400)))
//                    .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.errors", hasSize(1)))
//                    .andExpect(jsonPath("$.errors[0].field", is("emails")))
//                    .andExpect(jsonPath("$.errors[0].message", is(TestUtils.Message.EMAIL)));
//        }
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
        public void 招待状が取得できること() throws Exception {
            new NonStrictExpectations() {{
                clinicInvitationService.findClinicInvitationById("1");
                result = new ClinicInvitation("1", new Clinic(), new User(), "foo@localhost", "foo");
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
    @SpringApplicationConfiguration(classes = Application.class)
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
//
//        @Test
//        public void アクティベーションキーが入力されていない場合はエラーになること() throws Exception {
//            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception())))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status", is(400)))
//                    .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.errors", hasSize(1)))
//                    .andExpect(jsonPath("$.errors[0].field", is("activationKey")))
//                    .andExpect(jsonPath("$.errors[0].message", is(TestUtils.Message.EMPTY)));
//        }
//
//        @Test
//        public void アクティベーションキーの桁数が最大値を超えている場合はエラーになること() throws Exception {
//            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception(null, null, "1234567890123456789012345678901234567890123456789012345678901"))))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status", is(400)))
//                    .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.errors", hasSize(1)))
//                    .andExpect(jsonPath("$.errors[0].field", is("activationKey")))
//                    .andExpect(jsonPath("$.errors[0].rejected", is("1234567890123456789012345678901234567890123456789012345678901")))
//                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 60")));
//        }
//
//        @Test
//        public void アクティベートするユーザが招待されているユーザと違う場合はエラーになること() throws Exception {
//            new NonStrictExpectations() {{
//                SecurityUtils.getCurrentLogin();
//                result = "bar@localhost";
//                clinicInvitationRepository.findOne("1");
//                result = new ClinicInvitation("1", new Clinic(), new User(), "foo@localhost", "123456789012345678901234567890123456789012345678901234567890");
//            }};
//
//            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception(null, null, "123456789012345678901234567890123456789012345678901234567890"))))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status", is(400)))
//                    .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.errors", hasSize(1)))
//                    .andExpect(jsonPath("$.errors[0].field", is(nullValue())))
//                    .andExpect(jsonPath("$.errors[0].message", is(notNullValue())));
//        }
//
//        @Test
//        public void 招待状が存在しない場合はエラーになること() throws Exception {
//            new NonStrictExpectations() {{
//                SecurityUtils.getCurrentLogin();
//                result = "foo@localhost";
//                clinicInvitationRepository.findOne("1");
//                result = null;
//            }};
//
//            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
//                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
//                    .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception(null, null, "123456789012345678901234567890123456789012345678901234567890"))))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status", is(400)))
//                    .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
//                    .andExpect(jsonPath("$.errors", hasSize(1)))
//                    .andExpect(jsonPath("$.errors[0].field", is(nullValue())))
//                    .andExpect(jsonPath("$.errors[0].message", is(notNullValue())));
//        }
    }
}
