package org.majimena.petz.web.api.product;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.TestUtils;
import org.majimena.petz.WebAppTestConfiguration;
import org.majimena.petz.datatype.LangKey;
import org.majimena.petz.datatype.TaxType;
import org.majimena.petz.datatype.TimeZone;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;
import org.majimena.petz.security.PetzUser;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ProductService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 */
@RunWith(Enclosed.class)
public class ProductControllerTest {

    protected static Product createProduct1() {
        Product product1 = new Product();
        product1.setClinic(Clinic.builder().id("1").build());
        product1.setId("product1");
        product1.setName("Product Item 1");
        product1.setDescription("Product Item 1 Description");
        product1.setPrice(BigDecimal.valueOf(1000));
        product1.setTaxRate(BigDecimal.ONE.valueOf(0.08));
        product1.setTaxType(TaxType.EXCLUSIVE);
        product1.setTax(BigDecimal.valueOf(80));
        product1.setRemoved(Boolean.FALSE);
        return product1;
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetAllTest {

        private MockMvc mockMvc;

        @Mocked
        private ProductService productService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            ProductController sut = new ProductController();
            sut.setProductService(productService);
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
        }

        @Test
        public void プロダクトが検索できること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;
                productService.getProductsByProductCriteria(ProductCriteria.builder().clinicId("1").build());
                result = Arrays.asList(createProduct1());
            }};

            mockMvc.perform(get("/api/v1/clinics/1/products"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].id", is("product1")))
                    .andExpect(jsonPath("$.[0].name", is("Product Item 1")))
                    .andExpect(jsonPath("$.[0].description", is("Product Item 1 Description")))
                    .andExpect(jsonPath("$.[0].price", is(1000)))
                    .andExpect(jsonPath("$.[0].taxRate", is(0.08)))
                    .andExpect(jsonPath("$.[0].taxType", is(TaxType.EXCLUSIVE.name())))
                    .andExpect(jsonPath("$.[0].tax", is(80)))
                    .andExpect(jsonPath("$.[0].clinic", is(notNullValue())))
                    .andExpect(jsonPath("$.[0].clinic.id", is("1")))
                    .andExpect(jsonPath("$.[0].removed", is(false)));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PostTest {

        private MockMvc mockMvc;

        @Mocked
        private ProductService productService;

        @Mocked
        private ProductValidator productValidator;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            ProductController sut = new ProductController();
            sut.setProductService(productService);
            sut.setProductValidator(productValidator);
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
        }

        @Test
        public void プロダクトが登録されること() throws Exception {
            final Product data = createProduct1();

            new NonStrictExpectations() {{
                SecurityUtils.getPrincipal();
                result = Optional.of(new PetzUser("1", "username", "password", LangKey.JAPANESE, TimeZone.ASIA_TOKYO, Collections.<GrantedAuthority>emptyList()));
                productValidator.validate(data, (Errors) any);
                result = null;
                productService.saveProduct(data);
                result = data;
            }};

            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is("product1")))
                    .andExpect(jsonPath("$.name", is("Product Item 1")))
                    .andExpect(jsonPath("$.description", is("Product Item 1 Description")))
                    .andExpect(jsonPath("$.price", is(1000)))
                    .andExpect(jsonPath("$.taxRate", is(0.08)))
                    .andExpect(jsonPath("$.taxType", is(TaxType.EXCLUSIVE.name())))
                    .andExpect(jsonPath("$.tax", is(80)))
                    .andExpect(jsonPath("$.clinic", is(notNullValue())))
                    .andExpect(jsonPath("$.clinic.id", is("1")))
                    .andExpect(jsonPath("$.removed", is(false)));
        }
    }
}
