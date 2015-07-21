package org.majimena.petz.web.api.clinic;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.TestUtils;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicInvitation;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicInvitationAcception;
import org.majimena.petz.domain.clinic.ClinicInvitationRegistry;
import org.majimena.petz.repository.ClinicInvitationRepository;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicInvitationService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    public static class InviteTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicInvitationController sut;

        @Inject
        private ClinicInvitationRegistryValidator clinicInvitationRegistryValidator;

        @Inject
        private WebApplicationContext wac;

        @Mocked
        private ClinicInvitationService clinicInvitationService;

        @Mocked
        private UserRepository userRepository;

        @Mocked
        private ClinicRepository clinicRepository;

        @Mocked
        private ClinicStaffRepository clinicStaffRepository;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
            clinicInvitationRegistryValidator.setUserRepository(userRepository);
            clinicInvitationRegistryValidator.setClinicRepository(clinicRepository);
            clinicInvitationRegistryValidator.setClinicStaffRepository(clinicStaffRepository);
            sut.setClinicInvitationService(clinicInvitationService);
            sut.setClinicInvitationRegistryValidator(clinicInvitationRegistryValidator);
        }

        @Test
        public void 招待状を送信できること() throws Exception {
            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[]{"foo@localhost.com", "bar@localhost.com"});

            new NonStrictExpectations() {{
                clinicRepository.findOne("100");
                result = new Clinic();
                userRepository.findOneByLogin("foo@localhost.com");
                result = Optional.ofNullable(User.builder().id("1").build());
                clinicStaffRepository.findByClinicIdAndUserId("100", "1");
                result = Optional.ofNullable(null);
                userRepository.findOneByLogin("bar@localhost.com");
                result = Optional.ofNullable(null);
                clinicInvitationService.inviteStaff("100", new HashSet<>(Arrays.asList("foo@localhost.com", "bar@localhost.com")));
            }};

            mockMvc.perform(post("/api/v1/clinics/100/invitations")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isCreated());
        }

        @Test
        public void クリニックが存在しない場合は404になること() throws Exception {
            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[]{"foo@localhost.com"});

            new NonStrictExpectations() {{
                clinicRepository.findOne("100");
                result = null;
            }};

            mockMvc.perform(post("/api/v1/clinics/100/invitations")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
        }

        @Test
        public void 既にクリニックスタッフが存在している場合はエラーになること() throws Exception {
            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[]{"foo@localhost.com"});

            new NonStrictExpectations() {{
                clinicRepository.findOne("100");
                result = new Clinic();
                userRepository.findOneByLogin("foo@localhost.com");
                result = Optional.ofNullable(User.builder().id("1").build());
                clinicStaffRepository.findByClinicIdAndUserId("100", "1");
                result = Optional.ofNullable(new ClinicStaff());
            }};

            mockMvc.perform(post("/api/v1/clinics/100/invitations")
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
                .andExpect(jsonPath("$.errors[0].message", is(notNullValue())));
        }

        @Test
        public void メールアドレスが入力されていない場合はエラーになること() throws Exception {
            ClinicInvitationRegistry testData = new ClinicInvitationRegistry();

            mockMvc.perform(post("/api/v1/clinics/100/invitations")
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
            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[101]);

            mockMvc.perform(post("/api/v1/clinics/100/invitations")
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
            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[0]);

            mockMvc.perform(post("/api/v1/clinics/100/invitations")
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
            ClinicInvitationRegistry testData = new ClinicInvitationRegistry(null, new String[]{"1234567890"});

            mockMvc.perform(post("/api/v1/clinics/100/invitations")
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

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class ShowTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicInvitationController sut;

        @Inject
        private WebApplicationContext wac;

        @Mocked
        private ClinicInvitationService clinicInvitationService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
            sut.setClinicInvitationService(clinicInvitationService);
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

        @Inject
        private ClinicInvitationController sut;

        @Inject
        private ClinicInvitationAcceptionValidator clinicInvitationAcceptionValidator;

        @Inject
        private WebApplicationContext wac;

        @Mocked
        private ClinicInvitationService clinicInvitationService;

        @Mocked
        private ClinicRepository clinicRepository;

        @Mocked
        private ClinicInvitationRepository clinicInvitationRepository;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
            clinicInvitationAcceptionValidator.setClinicRepository(clinicRepository);
            clinicInvitationAcceptionValidator.setClinicInvitationRepository(clinicInvitationRepository);
            sut.setClinicInvitationService(clinicInvitationService);
            sut.setClinicInvitationAcceptionValidator(clinicInvitationAcceptionValidator);
        }

        @Test
        public void 招待をアクティベートできること() throws Exception {
            new NonStrictExpectations() {{
                clinicRepository.findOne("100");
                result = new Clinic();
                clinicInvitationRepository.findOne("1");
                result = new ClinicInvitation("1", new Clinic(), new User(), "foo@localhost", "foo");
                securityUtils.getCurrentLogin();
                result = "foo@localhost";
                clinicInvitationService.activate("1", "123456789012345678901234567890123456789012345678901234567890");
            }};

            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception(null, null, "123456789012345678901234567890123456789012345678901234567890"))))
                .andDo(print())
                .andExpect(status().isOk());
        }

        @Test
        public void クリニックが存在しない場合は404になること() throws Exception {
            new NonStrictExpectations() {{
                clinicRepository.findOne("100");
                result = null;
            }};

            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception(null, null, "123456789012345678901234567890123456789012345678901234567890"))))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
        }

        @Test
        public void アクティベーションキーが入力されていない場合はエラーになること() throws Exception {
            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("activationKey")))
                .andExpect(jsonPath("$.errors[0].message", is(TestUtils.Message.EMPTY)));
        }

        @Test
        public void アクティベーションキーの桁数が最大値を超えている場合はエラーになること() throws Exception {
            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception(null, null, "1234567890123456789012345678901234567890123456789012345678901"))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("activationKey")))
                .andExpect(jsonPath("$.errors[0].rejected", is("1234567890123456789012345678901234567890123456789012345678901")))
                .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 60")));
        }

        @Test
        public void アクティベートするユーザが招待されているユーザと違う場合はエラーになること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentLogin();
                result = "bar@localhost";
                clinicInvitationRepository.findOne("1");
                result = new ClinicInvitation("1", new Clinic(), new User(), "foo@localhost", "123456789012345678901234567890123456789012345678901234567890");
            }};

            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception(null, null, "123456789012345678901234567890123456789012345678901234567890"))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is(nullValue())))
                .andExpect(jsonPath("$.errors[0].message", is(notNullValue())));
        }

        @Test
        public void 招待状が存在しない場合はエラーになること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentLogin();
                result = "foo@localhost";
                clinicInvitationRepository.findOne("1");
                result = null;
            }};

            mockMvc.perform(put("/api/v1/clinics/100/invitations/1")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(new ClinicInvitationAcception(null, null, "123456789012345678901234567890123456789012345678901234567890"))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is(TestUtils.Title.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.detail", is(TestUtils.Detail.VALIDATION_FAILED)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is(nullValue())))
                .andExpect(jsonPath("$.errors[0].message", is(notNullValue())));
        }
    }
}
