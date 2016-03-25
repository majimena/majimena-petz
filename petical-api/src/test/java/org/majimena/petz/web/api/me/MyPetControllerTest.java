package org.majimena.petz.web.api.me;

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
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.pet.PetCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.PetService;
import org.majimena.petz.web.utils.PaginationUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see MyPetController
 */
@RunWith(Enclosed.class)
public class MyPetControllerTest {

    private static Pet newPet() {
        return Pet.builder()
            .id("pet1")
            .name("White")
            .user(User.builder().id("1").firstName("John").lastName("Jackson").build())
            .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetTest {

        private MockMvc mockMvc;
        @Tested
        private MyPetController sut = new MyPetController();
        @Injectable
        private PetService petService;
        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                .build();
        }

        @Test
        public void マイペットの一覧がページングで取得できること() throws Exception {
            PetCriteria criteria = PetCriteria.builder().userId("taro").build();
            Pageable pageable = PaginationUtils.generatePageRequest(1, 1);

            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "taro";
                petService.getPetsByPetCriteria(criteria, pageable);
                result = new PageImpl<>(Arrays.asList(newPet()), pageable, 2);
            }};

            mockMvc.perform(get("/api/v1/me/pets")
                .param("page", "1")
                .param("per_page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(header().string(HttpHeaders.LINK, "</api/v1/me/pets?page=2&per_page=1>; rel=\"next\",</api/v1/me/pets?page=2&per_page=1>; rel=\"last\",</api/v1/me/pets?page=1&per_page=1>; rel=\"first\""))
                .andExpect(jsonPath("$.[0].id", is("pet1")))
                .andExpect(jsonPath("$.[0].name", is("White")))
                .andExpect(jsonPath("$.[0].user.id", is("1")));
        }
    }
}
