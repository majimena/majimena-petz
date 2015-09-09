package org.majimena.petz.web.api.customer;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.majimena.petz.service.CustomerService;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Enclosed.class)
public class CustomerControllerTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class GetListTest {

        private MockMvc mockMvc;

        @Inject
        private CustomerController sut;

        @Mocked
        private CustomerService customerService;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
            sut.setCustomerService(customerService);
        }

        @Test
        public void ページングされてデータが取得できること() throws Exception {
            CustomerCriteria criteria = CustomerCriteria.builder().clinicId("c123").build();
            Pageable pageable = PaginationUtil.generatePageRequest(1, 1);

            final Clinic clinic = Clinic.builder().id("c123").build();
            final Customer d1 = Customer.builder().id("c111").clinic(clinic).user(User.builder().id("u111").build()).build();
            final Customer d2 = Customer.builder().id("c222").clinic(clinic).user(User.builder().id("u222").build()).build();

            new NonStrictExpectations() {{
                customerService.getCustomersByCustomerCriteria(criteria, pageable);
                result = new PageImpl(Arrays.asList(d1, d2), pageable, 2);
            }};

            mockMvc.perform(get("/api/v1/clinics/c123/customers")
                    .param("page", "1")
                    .param("per_page", "1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"))
                    .andExpect(jsonPath("$.[0].id", is("c111")))
                    .andExpect(jsonPath("$.[0].clinic.id", is("c123")))
                    .andExpect(jsonPath("$.[0].user.id", is("u111")))
                    .andExpect(jsonPath("$.[1].id", is("c222")))
                    .andExpect(jsonPath("$.[1].clinic.id", is("c123")))
                    .andExpect(jsonPath("$.[1].user.id", is("u222")));
        }
    }
}
