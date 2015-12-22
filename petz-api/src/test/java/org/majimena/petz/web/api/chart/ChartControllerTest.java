package org.majimena.petz.web.api.chart;

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
import org.majimena.petz.domain.Chart;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.chart.ChartCriteria;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ChartService;
import org.majimena.petz.service.UserService;
import org.majimena.petz.web.api.me.MyAccountController;
import org.majimena.petz.web.utils.PaginationUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see ChartController
 */
@RunWith(Enclosed.class)
public class ChartControllerTest {

    private static Chart newChart() {
        return Chart.builder()
            .id("chart1")
            .chartNo("1234567890")
            .clinic(Clinic.builder().id("1").build())
            .customer(Customer.builder().id("customer1").build())
            .pet(Pet.builder().id("pet1").build())
            .creationDate(LocalDateTime.of(2015, 12, 31, 0, 0, 0))
            .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;

        @Tested
        private ChartController sut = new ChartController();

        @Injectable
        private ChartService chartService;

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
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException("");
            }};

            mockMvc.perform(get("/api/v1/clinics/1/charts"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                .andExpect(jsonPath("$.title", is("Unauthorized")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void カルテの検索ができること() throws Exception {
            ChartCriteria criteria = ChartCriteria.builder().clinicId("1").build();
            Pageable pageable = PaginationUtils.generatePageRequest(1, 1);

            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "taro";
                chartService.findChartsByChartCriteria(criteria, pageable);
                result = new PageImpl<>(Arrays.asList(newChart()), pageable, 2);
            }};

            mockMvc.perform(get("/api/v1/clinics/1/charts")
                .param("page", "1")
                .param("per_page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(header().string(HttpHeaders.LINK, "</api/v1/clinics/1/charts?page=2&per_page=1>; rel=\"next\",</api/v1/clinics/1/charts?page=2&per_page=1>; rel=\"last\",</api/v1/clinics/1/charts?page=1&per_page=1>; rel=\"first\""))
                .andExpect(jsonPath("$.[0].id", is("chart1")))
                .andExpect(jsonPath("$.[0].chartNo", is("1234567890")))
                .andExpect(jsonPath("$.[0].creationDate", is("2015-12-31T00:00:00Z")))
                .andExpect(jsonPath("$.[0].clinic.id", is("1")))
                .andExpect(jsonPath("$.[0].customer.id", is("customer1")))
                .andExpect(jsonPath("$.[0].pet.id", is("pet1")));
        }
    }
}
