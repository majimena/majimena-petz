package org.majimena.petz.web.api.product;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.TestUtils;
import org.majimena.petz.WebAppTestConfiguration;
import org.majimena.petz.config.SpringMvcConfiguration;
import org.majimena.petz.datatype.TaxType;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ProductService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see ProductController
 */
@RunWith(Enclosed.class)
public class ProductControllerTest {

    // POST用テストデータ
    protected static Product newPostProduct() {
        return Product.builder()
                .name("12345678901234567890123456789012345678901234567890")
                .price(new BigDecimal(123456789))
                .taxType(TaxType.EXCLUSIVE)
                .taxRate(BigDecimal.valueOf(0.08))
                .description("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890").build();
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

            mockMvc.perform(get("/api/v1/clinics/1/products"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                    .andExpect(jsonPath("$.title", is("Unauthorized")))
                    .andExpect(jsonPath("$.status", is(401)))
                    .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void プロダクトが検索できること() throws Exception {
            Product data = newPostProduct();

            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;

                productService.getProductsByProductCriteria(ProductCriteria.builder().clinicId("1").build());
                data.setId("product1");
                data.setRemoved(Boolean.FALSE);
                data.setClinic(Clinic.builder().id("1").build());
                result = Arrays.asList(data);
            }};

            mockMvc.perform(get("/api/v1/clinics/1/products"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].id", is("product1")))
                    .andExpect(jsonPath("$.[0].name", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.[0].description", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.[0].price", is(123456789)))
                    .andExpect(jsonPath("$.[0].taxRate", is(0.08)))
                    .andExpect(jsonPath("$.[0].taxType", is(TaxType.EXCLUSIVE.name())))
                    .andExpect(jsonPath("$.[0].tax", is(123456789)))
                    .andExpect(jsonPath("$.[0].clinic", is(notNullValue())))
                    .andExpect(jsonPath("$.[0].clinic.id", is("1")))
                    .andExpect(jsonPath("$.[0].removed", is(Boolean.FALSE)));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;

        @Mocked
        private ProductService productService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            ProductController sut = new ProductController();
            sut.setProductService(productService);
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

            mockMvc.perform(get("/api/v1/clinics/1/products/product1"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                    .andExpect(jsonPath("$.title", is("Unauthorized")))
                    .andExpect(jsonPath("$.status", is(401)))
                    .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void プロダクトが検索できること() throws Exception {
            Product data = newPostProduct();

            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;

                productService.getProductByProductId("1", "product1");
                data.setId("product1");
                data.setRemoved(Boolean.FALSE);
                data.setClinic(Clinic.builder().id("1").build());
                result = Optional.of(data);
            }};

            mockMvc.perform(get("/api/v1/clinics/1/products/product1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("product1")))
                    .andExpect(jsonPath("$.name", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.description", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.price", is(123456789)))
                    .andExpect(jsonPath("$.taxRate", is(0.08)))
                    .andExpect(jsonPath("$.taxType", is(TaxType.EXCLUSIVE.name())))
                    .andExpect(jsonPath("$.tax", is(123456789)))
                    .andExpect(jsonPath("$.clinic", is(notNullValue())))
                    .andExpect(jsonPath("$.clinic.id", is("1")))
                    .andExpect(jsonPath("$.removed", is(Boolean.FALSE)));
        }

        @Test
        public void 検索結果がない場合は404になること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;

                productService.getProductByProductId("1", "product1");
                result = Optional.ofNullable(null);
            }};

            mockMvc.perform(get("/api/v1/clinics/1/products/product1"))
                    .andDo(print())
                    .andExpect(status().isNotFound());
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
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void 権限がない場合はアクセスできないこと() throws Exception {
            Product data = newPostProduct();

            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException("");
            }};

            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                    .andExpect(jsonPath("$.title", is("Unauthorized")))
                    .andExpect(jsonPath("$.status", is(401)))
                    .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void プロダクトが登録されること() throws Exception {
            Product data = newPostProduct();

            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;
                productValidator.validate(data, (Errors) any);
                result = null;

                productService.saveProduct(data);
                data.setId("product1");
                data.setRemoved(Boolean.FALSE);
                data.setClinic(Clinic.builder().id("1").build());
                result = data;
            }};

            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is("product1")))
                    .andExpect(jsonPath("$.name", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.description", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.price", is(123456789)))
                    .andExpect(jsonPath("$.taxRate", is(0.08)))
                    .andExpect(jsonPath("$.taxType", is(TaxType.EXCLUSIVE.name())))
                    .andExpect(jsonPath("$.tax", is(123456789)))
                    .andExpect(jsonPath("$.clinic", is(notNullValue())))
                    .andExpect(jsonPath("$.clinic.id", is("1")))
                    .andExpect(jsonPath("$.removed", is(Boolean.FALSE)));
        }

        @Test
        public void 名称にエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 未入力
            data.setName("");
            mockMvc.perform(post("/api/v1/clinics/1/products")
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
            mockMvc.perform(post("/api/v1/clinics/1/products")
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
        public void 説明にエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 桁数オーバー
            data.setDescription("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
            mockMvc.perform(post("/api/v1/clinics/1/products")
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

        @Test
        public void プライスにエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 未入力
            data.setPrice(null);
            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("price")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // 桁数オーバー
            data.setPrice(BigDecimal.valueOf(1234567890));
            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("price")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(1234567890)))
                    .andExpect(jsonPath("$.errors[0].message", is("numeric value out of bounds (<9 digits>.<0 digits> expected)")));
        }

        @Test
        public void 税区分にエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 未入力
            data.setTaxType(null);
            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("taxType")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // 型不正
            data.setTaxType(TaxType.EXCLUSIVE);
            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(new String(TestUtils.convertObjectToJsonBytes(data)).replace("EXCLUSIVE", "HOGE")))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Conversion Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send is probably malformed.")));
        }

        @Test
        public void 税率にエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 未入力
            data.setTaxRate(null);
            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("taxRate")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // 桁数オーバー１
            data.setTaxRate(BigDecimal.valueOf(1.123));
            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("taxRate")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(1.123)))
                    .andExpect(jsonPath("$.errors[0].message", is("numeric value out of bounds (<1 digits>.<2 digits> expected)")));

            // 桁数オーバー２
            data.setTaxRate(BigDecimal.valueOf(12.12));
            mockMvc.perform(post("/api/v1/clinics/1/products")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("taxRate")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(12.12)))
                    .andExpect(jsonPath("$.errors[0].message", is("numeric value out of bounds (<1 digits>.<2 digits> expected)")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PutTest {

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
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void 権限がない場合はアクセスできないこと() throws Exception {
            Product data = newPostProduct();

            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException("");
            }};

            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                    .andExpect(jsonPath("$.title", is("Unauthorized")))
                    .andExpect(jsonPath("$.status", is(401)))
                    .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void プロダクトが更新されること() throws Exception {
            final Product data = newPostProduct();

            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;
                productValidator.validate(data, (Errors) any);
                result = null;

                productService.updateProduct(data);
                data.setId("product1");
                data.setRemoved(Boolean.FALSE);
                data.setClinic(Clinic.builder().id("1").build());
                result = data;
            }};

            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("product1")))
                    .andExpect(jsonPath("$.name", is("12345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.description", is("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")))
                    .andExpect(jsonPath("$.price", is(123456789)))
                    .andExpect(jsonPath("$.taxRate", is(0.08)))
                    .andExpect(jsonPath("$.taxType", is(TaxType.EXCLUSIVE.name())))
                    .andExpect(jsonPath("$.tax", is(123456789)))
                    .andExpect(jsonPath("$.clinic", is(notNullValue())))
                    .andExpect(jsonPath("$.clinic.id", is("1")))
                    .andExpect(jsonPath("$.removed", is(Boolean.FALSE)));
        }

        @Test
        public void 名称にエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 未入力
            data.setName("");
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
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
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
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
        public void 説明にエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 桁数オーバー
            data.setDescription("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
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

        @Test
        public void プライスにエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 未入力
            data.setPrice(null);
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("price")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // 桁数オーバー
            data.setPrice(BigDecimal.valueOf(1234567890));
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("price")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(1234567890)))
                    .andExpect(jsonPath("$.errors[0].message", is("numeric value out of bounds (<9 digits>.<0 digits> expected)")));
        }

        @Test
        public void 税区分にエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 未入力
            data.setTaxType(null);
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("taxType")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // 型不正
            data.setTaxType(TaxType.EXCLUSIVE);
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(new String(TestUtils.convertObjectToJsonBytes(data)).replace("EXCLUSIVE", "HOGE")))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Conversion Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send is probably malformed.")));
        }

        @Test
        public void 税率にエラーがある場合はプロダクトが登録されないこと() throws Exception {
            Product data = newPostProduct();

            // 未入力
            data.setTaxRate(null);
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("taxRate")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(nullValue())))
                    .andExpect(jsonPath("$.errors[0].message", is("may not be null")));

            // 桁数オーバー１
            data.setTaxRate(BigDecimal.valueOf(1.123));
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("taxRate")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(1.123)))
                    .andExpect(jsonPath("$.errors[0].message", is("numeric value out of bounds (<1 digits>.<2 digits> expected)")));

            // 桁数オーバー２
            data.setTaxRate(BigDecimal.valueOf(12.12));
            mockMvc.perform(put("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content(TestUtils.convertObjectToJsonBytes(data)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/400")))
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.detail", is("The content you've send contains validation errors.")))
                    .andExpect(jsonPath("$.errors[0].field", is("taxRate")))
                    .andExpect(jsonPath("$.errors[0].rejected", is(12.12)))
                    .andExpect(jsonPath("$.errors[0].message", is("numeric value out of bounds (<1 digits>.<2 digits> expected)")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class DeleteTest {

        private MockMvc mockMvc;

        @Mocked
        private ProductService productService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            ProductController sut = new ProductController();
            sut.setProductService(productService);
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

            mockMvc.perform(delete("/api/v1/clinics/1/products/product1"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                    .andExpect(jsonPath("$.title", is("Unauthorized")))
                    .andExpect(jsonPath("$.status", is(401)))
                    .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void プロダクトが削除されること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;
                productService.deleteProductByProductId("1", "product1");
                result = null;
            }};

            mockMvc.perform(delete("/api/v1/clinics/1/products/product1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}
