package org.majimena.petz.web.api.customer;

import cz.jirutka.spring.exhandler.RestHandlerExceptionResolver;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.TestUtils;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.customer.CustomerCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.CustomerService;
import org.majimena.petz.web.api.customer.CustomerController;
import org.majimena.petz.web.api.customer.CustomerValidator;
import org.majimena.petz.web.rest.util.PaginationUtil;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see CustomerController
 */
@RunWith(Enclosed.class)
public class CustomerControllerTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class GetAllTest {

        private MockMvc mockMvc;

        @Inject
        private CustomerController sut;

        @Inject
        private RestHandlerExceptionResolver restHandlerExceptionResolver;

        @Mocked
        private CustomerService customerService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(sut)
                    .setHandlerExceptionResolvers(restHandlerExceptionResolver).build();
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
                SecurityUtils.isUserInClinic("c123");
                result = true;
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

        @Test
        public void 権限がない場合は401エラーとなること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.isUserInClinic("c123");
                result = false;
                customerService.getCustomersByCustomerCriteria((CustomerCriteria) any, (Pageable) any);
                times = 0;
            }};

            mockMvc.perform(get("/api/v1/clinics/c999/customers")
                    .param("page", "1")
                    .param("per_page", "1"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class GetTest {

        private MockMvc mockMvc;

        @Inject
        private CustomerController sut;

        @Inject
        private RestHandlerExceptionResolver restHandlerExceptionResolver;

        @Mocked
        private CustomerService customerService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(sut)
                    .setHandlerExceptionResolvers(restHandlerExceptionResolver).build();
            sut.setCustomerService(customerService);
        }

        @Test
        public void 該当するデータが取得できること() throws Exception {
            final Clinic clinic = Clinic.builder().id("c123").build();
            final Customer customer = Customer.builder().id("customer123").clinic(clinic).user(User.builder().id("u111").build()).build();

            new NonStrictExpectations() {{
                SecurityUtils.isUserInClinic("c123");
                result = true;
                customerService.getCustomerByCustomerId("customer123");
                result = Optional.ofNullable(customer);
            }};

            mockMvc.perform(get("/api/v1/clinics/c123/customers/customer123"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("customer123")))
                    .andExpect(jsonPath("$.clinic.id", is("c123")))
                    .andExpect(jsonPath("$.user.id", is("u111")));
        }

        @Test
        public void 該当するデータがない場合は404エラーとなること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.isUserInClinic("c123");
                result = true;
                customerService.getCustomerByCustomerId("customer123");
                result = Optional.ofNullable(null);
            }};

            mockMvc.perform(get("/api/v1/clinics/c123/customers/customer123"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        public void 権限がない場合は401エラーとなること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.isUserInClinic("c123");
                result = false;
                customerService.getCustomerByCustomerId("customer123");
                times = 0;
            }};

            mockMvc.perform(get("/api/v1/clinics/c999/customers/customer123"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void 取得したデータの権限が異なっている場合は401エラーとなること() throws Exception {
            final Clinic clinic = Clinic.builder().id("c999").build();
            final Customer customer = Customer.builder().id("customer123").clinic(clinic).user(User.builder().id("u111").build()).build();

            new NonStrictExpectations() {{
                SecurityUtils.isUserInClinic("c123");
                result = true;
                customerService.getCustomerByCustomerId("customer123");
                result = Optional.ofNullable(customer);
            }};

            mockMvc.perform(get("/api/v1/clinics/c123/customers/customer123"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class PostTest {

        private MockMvc mockMvc;

        @Inject
        private CustomerController sut;

        @Inject
        private RestHandlerExceptionResolver restHandlerExceptionResolver;

        @Mocked
        private CustomerValidator customerValidator;

        @Mocked
        private CustomerService customerService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(sut)
                    .setHandlerExceptionResolvers(restHandlerExceptionResolver).build();
            sut.setCustomerService(customerService);
            sut.setCustomerValidator(customerValidator);
        }

        @Test
        public void POSTしたデータが登録できること() throws Exception {
            final Customer customer = Customer.builder().lastName("Test").firstName("Taro")
                    .activated(Boolean.FALSE).blocked(Boolean.FALSE)
                    .clinic(Clinic.builder().id("c123").build())
                    .user(User.builder().login("test@example.com").email("test@example.com")
                            .state("Tokyo").city("Shinjuku-ku").street("Nishi-Shinjuku 1-1-1")
                            .phoneNo("0311112222").mobilePhoneNo("09011112222").build())
                    .build();

            new NonStrictExpectations() {{
                SecurityUtils.isUserInClinic("c123");
                result = true;
                customerValidator.validate(customer, (BindException) any);
                result = null;
                customerService.saveCustomer("c123", customer);
                result = customer;
            }};

            mockMvc.perform(post("/api/v1/clinics/c123/customers")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(customer)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.lastName", is("Test")))
                    .andExpect(jsonPath("$.firstName", is("Taro")))
                    .andExpect(jsonPath("$.activated", is(false)))
                    .andExpect(jsonPath("$.blocked", is(false)))
                    .andExpect(jsonPath("$.clinic.id", is("c123")))
                    .andExpect(jsonPath("$.user.login", is("test@example.com")))
                    .andExpect(jsonPath("$.user.email", is("test@example.com")))
                    .andExpect(jsonPath("$.user.state", is("Tokyo")))
                    .andExpect(jsonPath("$.user.city", is("Shinjuku-ku")))
                    .andExpect(jsonPath("$.user.street", is("Nishi-Shinjuku 1-1-1")))
                    .andExpect(jsonPath("$.user.phoneNo", is("0311112222")))
                    .andExpect(jsonPath("$.user.mobilePhoneNo", is("09011112222")));
        }

        @Test
        public void 権限がない場合は401エラーとなること() throws Exception {
            final Customer customer = Customer.builder().lastName("Test").firstName("Taro")
                    .activated(Boolean.FALSE).blocked(Boolean.FALSE)
                    .clinic(Clinic.builder().id("c123").build())
                    .user(User.builder().login("test@example.com").email("test@example.com")
                            .state("Tokyo").city("Shinjuku-ku").street("Nishi-Shinjuku 1-1-1")
                            .phoneNo("0311112222").mobilePhoneNo("09011112222").build())
                    .build();

            new NonStrictExpectations() {{
                SecurityUtils.isUserInClinic("c123");
                result = false;
            }};

            mockMvc.perform(post("/api/v1/clinics/c999/customers")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(customer)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class PutTest {

        private MockMvc mockMvc;

        @Inject
        private CustomerController sut;

        @Inject
        private RestHandlerExceptionResolver restHandlerExceptionResolver;

        @Mocked
        private CustomerValidator customerValidator;

        @Mocked
        private CustomerService customerService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(sut)
                    .setHandlerExceptionResolvers(restHandlerExceptionResolver).build();
            sut.setCustomerService(customerService);
            sut.setCustomerValidator(customerValidator);
        }

        @Test
        public void PUTしたデータが登録できること() throws Exception {
            final Customer customer = Customer.builder().id("customer123").lastName("Test").firstName("Taro")
                    .activated(Boolean.FALSE).blocked(Boolean.FALSE)
                    .clinic(Clinic.builder().id("c123").build())
                    .user(User.builder().id("u123").login("test@example.com").email("test@example.com")
                            .state("Tokyo").city("Shinjuku-ku").street("Nishi-Shinjuku 1-1-1")
                            .phoneNo("0311112222").mobilePhoneNo("09011112222").build())
                    .build();

            new NonStrictExpectations() {{
                SecurityUtils.isUserInClinic("c123");
                result = true;
                customerValidator.validate(customer, (BindException) any);
                result = null;
                customerService.saveCustomer("c123", customer);
                result = customer;
            }};

            mockMvc.perform(put("/api/v1/clinics/c123/customers/customer123")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(customer)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("customer123")))
                    .andExpect(jsonPath("$.lastName", is("Test")))
                    .andExpect(jsonPath("$.firstName", is("Taro")))
                    .andExpect(jsonPath("$.activated", is(false)))
                    .andExpect(jsonPath("$.blocked", is(false)))
                    .andExpect(jsonPath("$.clinic.id", is("c123")))
                    .andExpect(jsonPath("$.user.id", is("u123")))
                    .andExpect(jsonPath("$.user.login", is("test@example.com")))
                    .andExpect(jsonPath("$.user.email", is("test@example.com")))
                    .andExpect(jsonPath("$.user.state", is("Tokyo")))
                    .andExpect(jsonPath("$.user.city", is("Shinjuku-ku")))
                    .andExpect(jsonPath("$.user.street", is("Nishi-Shinjuku 1-1-1")))
                    .andExpect(jsonPath("$.user.phoneNo", is("0311112222")))
                    .andExpect(jsonPath("$.user.mobilePhoneNo", is("09011112222")));
        }

        @Test
        public void 権限がない場合は401エラーとなること() throws Exception {
            final Customer customer = Customer.builder().id("customer123").lastName("Test").firstName("Taro")
                    .activated(Boolean.FALSE).blocked(Boolean.FALSE)
                    .clinic(Clinic.builder().id("c123").build())
                    .user(User.builder().id("u123").login("test@example.com").email("test@example.com")
                            .state("Tokyo").city("Shinjuku-ku").street("Nishi-Shinjuku 1-1-1")
                            .phoneNo("0311112222").mobilePhoneNo("09011112222").build())
                    .build();

            new NonStrictExpectations() {{
                SecurityUtils.isUserInClinic("c123");
                result = false;
            }};

            mockMvc.perform(put("/api/v1/clinics/c999/customers/customer123")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(customer)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }
}
