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
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.service.ClinicService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by k.todoroki on 2015/08/12.
 */
@RunWith(Enclosed.class)
public class ClinicStaffControllerTest extends TestCase {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicStaffController sut;

        @Inject
        private WebApplicationContext webApplicationContext;

        @Mocked
        private ClinicService clinicService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            sut.setClinicService(clinicService);
        }

        @Test
        public void クリニックスタッフの一覧が取得できること() throws Exception {
            new NonStrictExpectations() {{
                clinicService.getClinicStaffsById("clinic1");
                result = Arrays.asList(ClinicStaff.builder().id("staff1").role("ROLE_CLINIC_ADMIN")
                        .user(User.builder().id("user1").build())
                        .clinic(Clinic.builder().id("clinic1").build())
                        .build());
            }};

            mockMvc.perform(get("/api/v1/clinics/clinic1/staffs"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].id", is("staff1")))
                    .andExpect(jsonPath("$.[0].role", is("ROLE_CLINIC_ADMIN")))
                    .andExpect(jsonPath("$.[0].user.id", is("user1")))
                    .andExpect(jsonPath("$.[0].clinic.id", is("clinic1")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class DeleteTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicStaffController sut;

        @Inject
        private WebApplicationContext webApplicationContext;

        @Mocked
        private ClinicService clinicService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            sut.setClinicService(clinicService);
        }

        @Test
        public void ペットが更新されること() throws Exception {
            new NonStrictExpectations() {{
                clinicService.deleteClinicStaff("clinic1", "staff1");
                result = null;
            }};

            mockMvc.perform(delete("/api/v1/clinics/clinic1/staffs/staff1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}