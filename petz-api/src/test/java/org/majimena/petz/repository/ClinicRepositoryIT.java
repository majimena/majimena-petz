package org.majimena.petz.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.domain.Clinic;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ClinicRepository
 */
@RunWith(Enclosed.class)
public class ClinicRepositoryIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class SampleTest extends AbstractSpringDBUnitTest {

        @Inject
        private ClinicRepository sut;

        @Inject
        private EntityManager entityManager;

        @Test
        @Transactional
        @DatabaseSetup("classpath:/testdata/clinic.xml")
        public void sampleTest() throws Exception {
            sut.save(Clinic.builder().name("name").description("description").email("email@localhost.com").build());

            List<Clinic> results = sut.findAll();
            assertThat(results.size(), is(3));
            Clinic result = results.get(results.size() - 1);
            assertThat(result.getId(), is(notNullValue()));
            assertThat(result.getEmail(), is("email@localhost.com"));
            assertThat(result.getName(), is("name"));
            assertThat(result.getDescription(), is("description"));
        }
    }
}
