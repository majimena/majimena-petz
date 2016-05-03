package org.majimena.petical.web.api.me;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petical.WebAppTestConfiguration;
import org.majimena.petical.config.SpringMvcConfiguration;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "taro";
                clinicService.getMyClinicsByUserId("taro");
                result = Arrays.asList(newClinic());
            }};

            mockMvc.perform(get("/api/v1/me/clinics")
                    .param("page", "1")
                    .param("per_page", "1")
                    .param("userId", "taro"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].id", is("1")))
                    .andExpect(jsonPath("$.[0].name", is("Test Clinic")))
                    .andExpect(jsonPath("$.[0].description", is("Test Clinic Description")))
                    .andExpect(jsonPath("$.[0].email", is("test@example.com")));
        }
    }
}
