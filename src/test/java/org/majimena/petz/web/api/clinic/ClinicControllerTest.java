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
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.service.ClinicService;
import org.majimena.petz.web.rest.ProjectResource;
import org.majimena.petz.web.rest.util.PaginationUtil;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProjectResource REST controller.
 *
 * @see ProjectResource
 */
@RunWith(Enclosed.class)
public class ClinicControllerTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class PostTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicController sut;

        @Mocked
        private ClinicService clinicService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
            sut.setClinicService(clinicService);
        }

        @Test
        public void サービスが呼び出されて正常終了すること() throws Exception {
            final Clinic testData = Clinic.builder().email("test@test.clinic").name("テストクリニック").description("テストクリニックの説明").build();
            final Clinic resultData = Clinic.builder().id("1").email("test@test.clinic").name("テストクリニック").description("テストクリニックの説明").build();

            new NonStrictExpectations() {{
                clinicService.saveClinic(testData);
                result = resultData;
            }};

            mockMvc.perform(post("/api/v1/clinics")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isCreated());
        }

        @Test
        public void 名称が入力されていない場合はエラーになること() throws Exception {
            final Clinic testData = Clinic.builder().email("test@test.clinic").build();

            mockMvc.perform(post("/api/v1/clinics")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }

        @Test
        public void メールアドレス形式ではない場合はエラーになること() throws Exception {
            final Clinic testData = Clinic.builder().name("テストクリニック").description("説明").email("test.clinic").build();

            mockMvc.perform(post("/api/v1/clinics")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class GetListTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicController sut;

        @Mocked
        private ClinicService clinicService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
            sut.setClinicService(clinicService);
        }

        @Test
        public void ページングされてデータが取得できること() throws Exception {
            final Clinic testData1 = Clinic.builder().email("test1.clinic").name("テストクリニック1").description("テストクリニック1の説明").build();
            final Clinic testData2 = Clinic.builder().email("test2.clinic").name("テストクリニック2").description("テストクリニック2の説明").build();
            final Pageable pageable = PaginationUtil.generatePageRequest(1, 1);
            new NonStrictExpectations() {{
                clinicService.getClinics(new ClinicCriteria(), pageable);
                result = new PageImpl(Arrays.asList(testData1, testData2), pageable, 2);
            }};

            mockMvc.perform(get("/api/v1/clinics")
                .param("page", "1")
                .param("per_page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"));
//                .andExpect(jsonPath("$.[*].id").value(hasItem(1)))
//                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
//                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicController sut;

        @Mocked
        private ClinicService clinicService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
            sut.setClinicService(clinicService);
        }

        @Test
        public void データが取得できること() throws Exception {
            new NonStrictExpectations() {{
                clinicService.getClinicById("1");
                result = Optional.of(Clinic.builder().id("1").email("test.clinic").name("テストクリニック").description("テストクリニックの説明").build());
            }};

            mockMvc.perform(get("/api/v1/clinics/{id}", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.email", is("test.clinic")))
                .andExpect(jsonPath("$.name", is("テストクリニック")))
                .andExpect(jsonPath("$.description", is("テストクリニックの説明")));
        }

        @Test
        public void 該当するデータがない場合は404エラーになること() throws Exception {
            new NonStrictExpectations() {{
                clinicService.getClinicById("0");
                result = Optional.empty();
            }};

            mockMvc.perform(get("/api/v1/clinics/{id}", "0"))
                .andDo(print())
                .andExpect(status().isNotFound());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class PutTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicController sut;

        @Mocked
        private ClinicService clinicService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
            sut.setClinicService(clinicService);
        }

        @Test
        public void updateProject() throws Exception {
            final Clinic testData = Clinic.builder().id("1").email("test@test.clinic").name("テストクリニック").description("テストクリニックの説明").build();
            final Clinic resultData = Clinic.builder().id("1").email("test@test.clinic").name("テストクリニック").description("テストクリニックの説明").build();

            new NonStrictExpectations() {{
                clinicService.updateClinic(testData);
                result = Optional.of(resultData);
            }};

            mockMvc.perform(put("/api/v1/clinics/1")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(testData)))
                .andDo(print())
                .andExpect(status().isOk());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class DeleteTest {

        private MockMvc mockMvc;

        @Inject
        private ClinicController sut;

        @Mocked
        private ClinicService clinicService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
            sut.setClinicService(clinicService);
        }

        @Test
        public void deleteProject() throws Exception {
            new NonStrictExpectations() {{
                clinicService.deleteClinic("1");
            }};

            mockMvc.perform(delete("/api/v1/clinics/{id}", "1")
                .accept(TestUtils.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk());
        }
    }
}
