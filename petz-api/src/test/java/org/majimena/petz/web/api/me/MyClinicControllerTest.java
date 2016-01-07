package org.majimena.petz.web.api.me;

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
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.clinic.ClinicCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ClinicService;
import org.majimena.petz.web.utils.PaginationUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see MyClinicController
 */
@RunWith(Enclosed.class)
public class MyClinicControllerTest {

    private static Clinic newClinic() {
        return Clinic.builder()
                .id("1")
                .name("Test Clinic")
                .description("Test Clinic Description")
                .email("test@example.com")
                .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;
        @Tested
        private MyClinicController sut = new MyClinicController();
        @Injectable
        private ClinicService clinicService;
        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void マイクリニックの一覧がページングで取得できること() throws Exception {
            ClinicCriteria criteria = ClinicCriteria.builder().userId("taro").build();
            Pageable pageable = PaginationUtils.generatePageRequest(1, 1);

            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "taro";
                clinicService.findMyClinicsByClinicCriteria(criteria, pageable);
                result = new PageImpl<>(Arrays.asList(newClinic()), pageable, 2);
            }};

            mockMvc.perform(get("/api/v1/me/clinics")
                    .param("page", "1")
                    .param("per_page", "1")
                    .param("userId", "taro"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-Total-Count", "2"))
                    .andExpect(header().string(HttpHeaders.LINK, "</api/v1/me/clinics?page=2&per_page=1>; rel=\"next\",</api/v1/me/clinics?page=2&per_page=1>; rel=\"last\",</api/v1/me/clinics?page=1&per_page=1>; rel=\"first\""))
                    .andExpect(jsonPath("$.[0].id", is("1")))
                    .andExpect(jsonPath("$.[0].name", is("Test Clinic")))
                    .andExpect(jsonPath("$.[0].description", is("Test Clinic Description")))
                    .andExpect(jsonPath("$.[0].email", is("test@example.com")));
        }
    }
}
