package org.majimena.petz.service.impl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.datatype.TaxType;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;
import org.majimena.petz.repository.AbstractSpringDBUnitTest;
import org.majimena.petz.service.ProductService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see ProductServiceImpl
 */
@RunWith(Enclosed.class)
public class ProductServiceImplIT {

    @Transactional
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class GetProductsByProductCriteriaTest extends AbstractSpringDBUnitTest {

        @Inject
        private ProductService sut;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void プロダクトの一覧が取得できること() throws Exception {
            ProductCriteria criteria = new ProductCriteria("1", "初診");
            List<Product> result = sut.getProductsByProductCriteria(criteria);

            assertThat(result.isEmpty(), is(false));
            assertThat(result.size(), is(2));
            assertThat(result.get(0).getId(), is("product11"));
            assertThat(result.get(0).getName(), is("初診料"));
            assertThat(result.get(0).getPrice(), is(BigDecimal.valueOf(2000)));
            assertThat(result.get(0).getTaxType(), is(TaxType.EXCLUSIVE));
            assertThat(result.get(0).getTaxRate(), is(BigDecimal.valueOf(0.08)));
            assertThat(result.get(0).getTax(), is(BigDecimal.valueOf(160)));
            assertThat(result.get(0).getDescription(), is("初診料の説明"));
            assertThat(result.get(0).getRemoved(), is(false));
            assertThat(result.get(1).getId(), is("product12"));
            assertThat(result.get(1).getName(), is("初診料"));
            assertThat(result.get(1).getPrice(), is(BigDecimal.valueOf(2000)));
            assertThat(result.get(1).getTaxType(), is(TaxType.EXCLUSIVE));
            assertThat(result.get(1).getTaxRate(), is(BigDecimal.valueOf(0.08)));
            assertThat(result.get(1).getTax(), is(BigDecimal.valueOf(160)));
            assertThat(result.get(1).getDescription(), is("初診料の説明"));
            assertThat(result.get(1).getRemoved(), is(true));
        }

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void プロダクトが存在しない場合は何も取得できないこと() throws Exception {
            ProductCriteria criteria = new ProductCriteria("2", "foo");
            List<Product> result = sut.getProductsByProductCriteria(criteria);

            assertThat(result.isEmpty(), is(true));
        }
    }
}
