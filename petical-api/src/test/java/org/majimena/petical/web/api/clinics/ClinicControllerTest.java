package org.majimena.petical.web.api.clinics;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petical.TestUtils;
import org.majimena.petical.WebAppTestConfiguration;
import org.majimena.petical.config.SpringMvcConfiguration;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.clinic.ClinicCriteria;
import org.majimena.petical.security.ResourceCannotAccessException;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicService;
import org.majimena.petical.web.utils.PaginationUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see ClinicController
 */
@RunWith(Enclosed.class)
public class ClinicControllerTest {

    private static Clinic newClinic() {
        return Clinic.builder()
                .name("12345678901234567890123456789012345678901234567890")
                .firstName("12345678901234567890123456789012345678901234567890")
                .lastName("12345678901234567890123456789012345678901234567890")
                .country("JP")
                .zipCode("1234567890")
                .state("12345678901234567890123456789012345678901234567890")
                .city("12345678901234567890123456789012345678901234567890")
                .street("12345678901234567890123456789012345678901234567890")
                .phoneNo("123456789012345")
                .email("foo@bar.com")
                .description("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetListTest {

        private MockMvc mockMvc;
        @Tested
        private ClinicController sut = new ClinicController();
        @Injectable
        private ClinicService clinicService;
        @Injectable
        private ClinicValidator clinicValidator;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void ページングされてデータが取得できること() throws Exception {
            Clinic testData1 = Clinic.builder().email("test1.clinic").name("テストクリニック1").description("テストクリニック1の説明").build();
            Clinic testData2 = Clinic.builder().email("test2.clinic").name("テストクリニック2").description("テストクリニック2の説明").build();
            Pageable pageable = PaginationUtils.generatePageRequest(1, 1);
            new NonStrictExpectations() {{
                clinicService.findClinicsByClinicCriteria(new ClinicCriteria(), pageable);
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
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;
        @Tested
        private ClinicController sut = new ClinicController();
        @Injectable
        private ClinicService clinicService;
        @Injectable
        private ClinicValidator clinicValidator;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
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
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PostTest {

        private MockMvc mockMvc;
        @Tested
        private ClinicController sut = new ClinicController();
        @Injectable
        private ClinicService clinicService;
        @Injectable
        private ClinicValidator clinicValidator;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void クリニックが正常に登録できること() throws Exception {
            Clinic data = newClinic();

            new NonStrictExpectations() {{
                clinicService.saveClinic(data);
                data.setId("1");
                data.setRemoved(Boolean.FALSE);
                result = data;
            }};

            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is("1")))
                    .andExpect(jsonPath("$.name", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.firstName", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.lastName", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.country", is("JP")))
                    .andExpect(jsonPath("$.zipCode", is("1234567890")))
                    .andExpect(jsonPath("$.state", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.city", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.street", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.phoneNo", is("123456789012345")))
                    .andExpect(jsonPath("$.email", is("foo@bar.com")))
                    .andExpect(jsonPath("$.description", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.removed", is(Boolean.FALSE)));
        }

        @Test
        public void 名称にエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 未入力
            data.setName("");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("name")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setName("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("name")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 姓にエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 未入力
            data.setLastName("");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("lastName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setLastName("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("lastName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 名にエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 未入力
            data.setFirstName("");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("firstName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setFirstName("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("firstName")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 郵便番号にエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 桁数オーバー
            data.setZipCode("12345678901");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("zipCode")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("12345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 10")));

            // 型エラー
            data.setZipCode("12345abcde");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("zipCode")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("12345abcde")))
                    .andExpect(jsonPath("$.errors[0].message", is("must match \"^[0-9]+$\"")));
        }

        @Test
        public void 都道府県にエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 未入力
            data.setState("");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("state")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setState("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("state")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 市区町村にエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 未入力
            data.setCity("");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("city")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be empty")));

            // 桁数オーバー
            data.setCity("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("city")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 丁目番地にエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 桁数オーバー
            data.setStreet("123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("street")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 50")));
        }

        @Test
        public void 電話番号にエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 桁数オーバー
            data.setPhoneNo("1234567890123456");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("phoneNo")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("1234567890123456")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 15")));

            // 型エラー
            data.setPhoneNo("1234-1234-1234");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("phoneNo")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("1234-1234-1234")))
                    .andExpect(jsonPath("$.errors[0].message", is("must match \"^[0-9]+$\"")));
        }

        @Test
        public void メールアドレスにエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 型エラー
            data.setEmail("foobar.at.bar.com");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("email")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("foobar.at.bar.com")))
                    .andExpect(jsonPath("$.errors[0].message", is("not a well-formed email address")));
        }

        @Test
        public void 説明にエラーがある場合は登録されないこと() throws Exception {
            Clinic data = newClinic();

            // 桁数オーバー
            data.setDescription("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("description")))
                    .andExpect(jsonPath("$.errors[0].rejected", is("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901")))
                    .andExpect(jsonPath("$.errors[0].message", is("size must be between 0 and 2000")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PutTest {

        private MockMvc mockMvc;
        @Tested
        private ClinicController sut = new ClinicController();
        @Injectable
        private ClinicService clinicService;
        @Injectable
        private ClinicValidator clinicValidator;
        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void クリニックを正常に更新できること() throws Exception {
            Clinic data = newClinic();
            data.setId("1");
            data.setRemoved(Boolean.FALSE);

            new NonStrictExpectations() {{
                clinicValidator.validate(data, new BindException(data, "clinic"));
                result = null;
                clinicService.updateClinic(data);
                result = data;
            }};

            mockMvc.perform(put("/api/v1/clinics/1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("1")))
                    .andExpect(jsonPath("$.name", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.firstName", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.lastName", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.country", is("JP")))
                    .andExpect(jsonPath("$.zipCode", is("1234567890")))
                    .andExpect(jsonPath("$.state", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.city", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.street", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.phoneNo", is("123456789012345")))
                    .andExpect(jsonPath("$.email", is("foo@bar.com")))
                    .andExpect(jsonPath("$.description", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.removed", is(Boolean.FALSE)));
        }

        @Test
        public void 権限がない場合は401エラーになること() throws Exception {
            Clinic data = newClinic();
            data.setId("1");
            data.setRemoved(Boolean.FALSE);

            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException();
            }};

            mockMvc.perform(put("/api/v1/clinics/1")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(data)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                .andExpect(jsonPath("$.title", is("Unauthorized")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class DeleteTest {

        private MockMvc mockMvc;
        @Tested
        private ClinicController sut = new ClinicController();
        @Injectable
        private ClinicService clinicService;
        @Injectable
        private ClinicValidator clinicValidator;
        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void クリニックが削除できること() throws Exception {
            new NonStrictExpectations() {{
                clinicService.deleteClinic("1");
                result = null;
            }};

            mockMvc.perform(delete("/api/v1/clinics/1")
                    .accept(TestUtils.APPLICATION_JSON_UTF8))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        public void 権限がない場合は401エラーになること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException();
            }};

            mockMvc.perform(delete("/api/v1/clinics/1")
                .accept(TestUtils.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                .andExpect(jsonPath("$.title", is("Unauthorized")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }
    }
}
