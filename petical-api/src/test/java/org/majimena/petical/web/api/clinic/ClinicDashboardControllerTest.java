package org.majimena.petical.web.api.clinic;

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
import org.majimena.petical.domain.clinic.ClinicOutline;
import org.majimena.petical.domain.clinic.ClinicOutlineCriteria;
import org.majimena.petical.security.ResourceCannotAccessException;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see ClinicDashboardController
 */
@RunWith(Enclosed.class)
public class ClinicDashboardControllerTest {

    private static ClinicOutline newClinicOutline() {
        return ClinicOutline.builder()
            .chart(BigDecimal.valueOf(100))
            .reserve(BigDecimal.valueOf(10))
            .examinated(BigDecimal.valueOf(20))
            .sales(BigDecimal.valueOf(439300))
            .build();
    }

    private static ClinicOutlineCriteria newClinicOutlineCriteria() {
        return ClinicOutlineCriteria.builder()
            .clinicId("1")
            .year(2015)
            .month(12)
            .day(31)
            .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;
        @Tested
        private ClinicDashboardController sut = new ClinicDashboardController();
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
        public void データが取得できること() throws Exception {
            ClinicOutlineCriteria criteria = newClinicOutlineCriteria();
            new NonStrictExpectations() {{
                clinicService.findClinicOutlineByClinicOutlineCriteria(criteria);
                result = Optional.of(newClinicOutline());
            }};

            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", String.valueOf(criteria.getYear()))
                .param("month", String.valueOf(criteria.getMonth()))
                .param("day", String.valueOf(criteria.getDay())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chart", is(100)))
                .andExpect(jsonPath("$.reserve", is(10)))
                .andExpect(jsonPath("$.examinated", is(20)))
                .andExpect(jsonPath("$.sales", is(439300)));
        }

        @Test
        public void 年にエラーがある場合は400エラーになること() throws Exception {
            // 指定しない
            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("month", "12")
                .param("day", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                .andExpect(jsonPath("$.errors[0].field", is("year")))
                .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // 最小値より小さい
            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", "2009")
                .param("month", "12")
                .param("day", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                .andExpect(jsonPath("$.errors[0].field", is("year")))
                .andExpect(jsonPath("$.errors[0].rejected", is(2009)))
                .andExpect(jsonPath("$.errors[0].message", is("must be greater than or equal to 2010")));

            // 最大値より大きい
            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", "3000")
                .param("month", "12")
                .param("day", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                .andExpect(jsonPath("$.errors[0].field", is("year")))
                .andExpect(jsonPath("$.errors[0].rejected", is(3000)))
                .andExpect(jsonPath("$.errors[0].message", is("must be less than or equal to 2999")));
        }

        @Test
        public void 月にエラーがある場合は400エラーになること() throws Exception {
            // 指定しない
            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", "2010")
                .param("day", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                .andExpect(jsonPath("$.errors[0].field", is("month")))
                .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // 最小値より小さい
            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", "2010")
                .param("month", "0")
                .param("day", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                .andExpect(jsonPath("$.errors[0].field", is("month")))
                .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                .andExpect(jsonPath("$.errors[0].message", is("must be greater than or equal to 1")));

            // 最大値より大きい
            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", "2010")
                .param("month", "13")
                .param("day", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                .andExpect(jsonPath("$.errors[0].field", is("month")))
                .andExpect(jsonPath("$.errors[0].rejected", is(13)))
                .andExpect(jsonPath("$.errors[0].message", is("must be less than or equal to 12")));
        }

        @Test
        public void 日にエラーがある場合は400エラーになること() throws Exception {
            // 指定しない
            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", "2010")
                .param("month", "12"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                .andExpect(jsonPath("$.errors[0].field", is("day")))
                .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // 最小値より小さい
            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", "2010")
                .param("month", "12")
                .param("day", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                .andExpect(jsonPath("$.errors[0].field", is("day")))
                .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                .andExpect(jsonPath("$.errors[0].message", is("must be greater than or equal to 1")));

            // 最大値より大きい
            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", "2010")
                .param("month", "12")
                .param("day", "32"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                .andExpect(jsonPath("$.errors[0].field", is("day")))
                .andExpect(jsonPath("$.errors[0].rejected", is(32)))
                .andExpect(jsonPath("$.errors[0].message", is("must be less than or equal to 31")));
        }

        @Test
        public void 参照できないデータの場合は401エラーになること() throws Exception {
            ClinicOutlineCriteria criteria = newClinicOutlineCriteria();
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException();
            }};

            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", String.valueOf(criteria.getYear()))
                .param("month", String.valueOf(criteria.getMonth()))
                .param("day", String.valueOf(criteria.getDay())))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                .andExpect(jsonPath("$.title", is("Unauthorized")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void 該当するデータがない場合は404エラーになること() throws Exception {
            ClinicOutlineCriteria criteria = newClinicOutlineCriteria();
            new NonStrictExpectations() {{
                clinicService.findClinicOutlineByClinicOutlineCriteria(criteria);
                result = Optional.empty();
            }};

            mockMvc.perform(get("/api/v1/clinics/{id}/outline", "1")
                .param("year", String.valueOf(criteria.getYear()))
                .param("month", String.valueOf(criteria.getMonth()))
                .param("day", String.valueOf(criteria.getDay())))
                .andDo(print())
                .andExpect(status().isNotFound());
        }
    }
}
