package org.majimena.petz.web.api.pet;

import com.google.common.collect.Sets;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.TestUtils;
import org.majimena.petz.datatypes.SexType;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.PetService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by todoken on 2015/07/26.
 */
@RunWith(Enclosed.class)
public class PetControllerTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class PostTest {

        private MockMvc mockMvc;

        @Inject
        private PetController sut;

        @Inject
        private WebApplicationContext webApplicationContext;

        @Mocked
        private PetService petService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            sut.setPetService(petService);
        }

        @Test
        public void ペットが登録されること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "1";
            }};

            mockMvc.perform(post("/api/v1/pets")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content("{\"birthDate\":\"2015-02-27T15:00:00.000+09:00\",\"user\":{\"id\":\"1\"},\"sex\":\"MALE\",\"tags\":[\"タグ２\",\"タグ１\"],\"types\":[\"タイプ１\",\"タイプ２\"],\"name\":\"ポチ\",\"profile\":\"プロファイル\"}"))
//                .content("{\"user\":{\"id\":\"1\"},\"sex\":\"MALE\",\"tags\":[\"タグ２\",\"タグ１\"],\"types\":[\"タイプ１\",\"タイプ２\"],\"name\":\"ポチ\",\"profile\":\"プロファイル\"}"))
                .andDo(print())
                .andExpect(status().isCreated());

            new Verifications() {{
                Pet pet;
                petService.savePet(pet = withCapture());
                System.out.println(pet.toString());

                assertThat(pet.getId(), is(nullValue()));
                assertThat(pet.getName(), is("ポチ"));
                assertThat(pet.getProfile(), is("プロファイル"));
                assertThat(pet.getSex(), is(SexType.MALE));
//                assertThat(pet.getBirthDate(), is(LocalDateTime.of(2015, 2, 27, 15, 0, 0)));
                assertThat(pet.getUser().getId(), is("1"));
                assertThat(pet.getTags(), is(Sets.newHashSet("タグ１", "タグ２")));
                assertThat(pet.getTypes(), is(Sets.newHashSet("タイプ１", "タイプ２")));
            }};
        }
    }

}
