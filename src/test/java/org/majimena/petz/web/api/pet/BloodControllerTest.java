package org.majimena.petz.web.api.pet;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.TestUtils;
import org.majimena.petz.domain.Blood;
import org.majimena.petz.repository.BloodRepository;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by todoken on 2015/07/26.
 */
@RunWith(Enclosed.class)
public class BloodControllerTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class PostTest {

        private MockMvc mockMvc;

        @Inject
        private BloodController sut;

        @Inject
        private WebApplicationContext wac;

        @Mocked
        private BloodRepository bloodRepository;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
            sut.setBloodRepository(bloodRepository);
        }

        @Test
        public void サービスが呼び出されて正常終了すること() throws Exception {
            new NonStrictExpectations() {{
                bloodRepository.findAll();
                result = Arrays.asList(new Blood("foo"), new Blood("bar"));
            }};

            mockMvc.perform(get("/api/v1/bloods")
                .contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]", is("foo")))
                .andExpect(jsonPath("$.[1]", is("bar")));
        }
    }
}
