package org.majimena.petz.web.api.pet;

import com.google.common.collect.Sets;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.TestUtils;
import org.majimena.petz.datatypes.SexType;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Tag;
import org.majimena.petz.domain.Type;
import org.majimena.petz.domain.User;
import org.majimena.petz.service.PetService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

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

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            sut.setPetService(petService);
        }

        @Test
        public void ペットが登録されること() throws Exception {
            final Pet testData = Pet.builder().name("ポチ").profile("プロファイル")
                .user(User.builder().id("1").build())
                .sex(SexType.MALE).types(Sets.newHashSet(new Type("タイプ１"), new Type("タイプ２")))
                .tags(Sets.newHashSet(new Tag("タグ１"), new Tag("タグ２"))).build();

            new NonStrictExpectations() {{
                petService.savePet(testData);
                result = testData;
            }};

            mockMvc.perform(post("/api/v1/pets")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content("{\"birthDate\":\"2015-02-27T15:00:00.000Z\",\"user\":{\"id\":\"1\"},\"sex\":{\"value\":\"MALE\",\"name\":\"オス\"},\"tags\":[{\"name\":\"タグ２\"},{\"name\":\"タグ１\"}],\"types\":[{\"name\":\"タイプ１\"},{\"name\":\"タイプ２\"}],\"name\":\"ポチ\",\"profile\":\"プロファイル\"}"))
                .andDo(print())
                .andExpect(status().isCreated());
        }
    }

}
