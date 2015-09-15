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
import org.majimena.petz.domain.*;
import org.majimena.petz.domain.common.SexType;
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
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by todoken on 2015/07/26.
 */
@RunWith(Enclosed.class)
public class PetControllerTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class GetTest {

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
        public void ログインユーザのペットが取得できること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "u1";
                petService.findPetsByUserId("u1");
                result = Arrays.asList(Pet.builder().id("p1").name("test data").profile("test data's profile")
                    .birthDate(LocalDateTime.of(2015, 2, 27, 15, 0)).sex(SexType.MALE)
                    .type(new Type("type1")).tags(Sets.newHashSet(new Tag("tag1"), new Tag("tag2")))
                    .user(User.builder().id("u1").build())
                    .build());
            }};

            mockMvc.perform(get("/api/v1/pets"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is("p1")))
                .andExpect(jsonPath("$.[0].name", is("test data")))
                .andExpect(jsonPath("$.[0].user.id", is("u1")))
                .andExpect(jsonPath("$.[0].birthDate", is("2015-02-27T15:00:00+09:00")))
                .andExpect(jsonPath("$.[0].sex", is("MALE")))
                .andExpect(jsonPath("$.[0].profile", is("test data's profile")))
                .andExpect(jsonPath("$.[0].type", is("type1")))
                .andExpect(jsonPath("$.[0].tags[0]", is("tag1")))
                .andExpect(jsonPath("$.[0].tags[1]", is("tag2")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class ShowTest {

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
        public void ログインユーザのペットが取得できること() throws Exception {
            new NonStrictExpectations() {{
                petService.findPetByPetId("p1");
                result = Pet.builder().id("p1").name("test data").profile("test data's profile")
                    .birthDate(LocalDateTime.of(2015, 2, 27, 15, 0)).sex(SexType.MALE)
                    .type(new Type("type1")).tags(Sets.newHashSet(new Tag("tag1"), new Tag("tag2")))
                    .user(User.builder().id("u1").build())
                    .build();
            }};

            mockMvc.perform(get("/api/v1/pets/p1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("p1")))
                .andExpect(jsonPath("$.name", is("test data")))
                .andExpect(jsonPath("$.user.id", is("u1")))
                .andExpect(jsonPath("$.birthDate", is("2015-02-27T15:00:00+09:00")))
                .andExpect(jsonPath("$.sex", is("MALE")))
                .andExpect(jsonPath("$.profile", is("test data's profile")))
                .andExpect(jsonPath("$.type", is("type1")))
                .andExpect(jsonPath("$.tags[0]", is("tag1")))
                .andExpect(jsonPath("$.tags[1]", is("tag2")));
        }
    }

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
                .content("{\"birthDate\":\"2015-02-27T15:00:00.000+09:00\",\"microchipDate\":\"2015-02-27T15:00:00.000+09:00\",\"microchipNo\":\"1234567890\"," +
                    "\"user\":{\"id\":\"1\"},\"type\":\"トイプードル\",\"color\":\"ホワイト\",\"blood\":\"DEA1.1\"," +
                    "\"sex\":\"MALE\",\"tags\":[\"血統書付き\",\"室内犬\"],\"neutral\":\"true\"," +
                    "\"name\":\"ポチ\",\"profile\":\"プロファイル\",\"allergia\":\"アレルギー\",\"drug\":\"薬剤\",\"other\":\"その他\"}"))
                .andDo(print())
                .andExpect(status().isCreated());

            new Verifications() {{
                Pet pet;
                petService.savePet(pet = withCapture());
                System.out.println(pet.toString());

                assertThat(pet.getId(), is(nullValue()));
                assertThat(pet.getName(), is("ポチ"));
                assertThat(pet.getUser().getId(), is("1"));
                assertThat(pet.getTags(), is(Sets.newHashSet(new Tag("血統書付き"), new Tag("室内犬"))));
                assertThat(pet.getColor(), is(new Color("ホワイト")));
                assertThat(pet.getType(), is(new Type("トイプードル")));
                assertThat(pet.getBlood(), is(new Blood("DEA1.1")));
                assertThat(pet.getProfile(), is("プロファイル"));
                assertThat(pet.getAllergia(), is("アレルギー"));
                assertThat(pet.getDrug(), is("薬剤"));
                assertThat(pet.getOther(), is("その他"));
                assertThat(pet.getSex(), is(SexType.MALE));
                assertThat(pet.getBirthDate(), is(LocalDateTime.of(2015, 2, 27, 15, 0, 0)));
                assertThat(pet.getMicrochipDate(), is(LocalDateTime.of(2015, 2, 27, 15, 0, 0)));
                assertThat(pet.getMicrochipNo(), is("1234567890"));
            }};
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = Application.class)
    @WebAppConfiguration
    public static class PutTest {

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
        public void ペットが更新されること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "1";
            }};

            mockMvc.perform(put("/api/v1/pets/p1")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content("{\"birthDate\":\"2015-02-27T15:00:00.000+09:00\",\"microchipDate\":\"2015-02-27T15:00:00.000+09:00\",\"microchipNo\":\"1234567890\"," +
                    "\"user\":{\"id\":\"1\"},\"type\":\"トイプードル\",\"color\":\"ホワイト\",\"blood\":\"DEA1.1\"," +
                    "\"sex\":\"MALE\",\"tags\":[\"血統書付き\",\"室内犬\"],\"neutral\":\"true\"," +
                    "\"name\":\"ポチ\",\"profile\":\"プロファイル\",\"allergia\":\"アレルギー\",\"drug\":\"薬剤\",\"other\":\"その他\"}"))
                .andDo(print())
                .andExpect(status().isOk());

            new Verifications() {{
                Pet pet;
                petService.savePet(pet = withCapture());

                assertThat(pet.getId(), is("p1"));
                assertThat(pet.getName(), is("ポチ"));
                assertThat(pet.getUser().getId(), is("1"));
                assertThat(pet.getTags(), is(Sets.newHashSet(new Tag("血統書付き"), new Tag("室内犬"))));
                assertThat(pet.getColor(), is(new Color("ホワイト")));
                assertThat(pet.getType(), is(new Type("トイプードル")));
                assertThat(pet.getBlood(), is(new Blood("DEA1.1")));
                assertThat(pet.getProfile(), is("プロファイル"));
                assertThat(pet.getAllergia(), is("アレルギー"));
                assertThat(pet.getDrug(), is("薬剤"));
                assertThat(pet.getOther(), is("その他"));
                assertThat(pet.getSex(), is(SexType.MALE));
                assertThat(pet.getBirthDate(), is(LocalDateTime.of(2015, 2, 27, 15, 0, 0)));
                assertThat(pet.getMicrochipDate(), is(LocalDateTime.of(2015, 2, 27, 15, 0, 0)));
                assertThat(pet.getMicrochipNo(), is("1234567890"));
            }};
        }
    }
}
