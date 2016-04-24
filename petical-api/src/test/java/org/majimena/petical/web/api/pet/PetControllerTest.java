package org.majimena.petical.web.api.pet;

import com.google.common.collect.Sets;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petical.TestUtils;
import org.majimena.petical.WebAppTestConfiguration;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.datatype.SexType;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.domain.Blood;
import org.majimena.petical.domain.Color;
import org.majimena.petical.domain.Pet;
import org.majimena.petical.domain.Tag;
import org.majimena.petical.domain.Type;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.authentication.PetzUserKey;
import org.majimena.petical.domain.pet.PetCriteria;
import org.majimena.petical.domain.authentication.PetzUser;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.PetService;
import org.majimena.petical.web.utils.PaginationUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see PetController
 */
@RunWith(Enclosed.class)
public class PetControllerTest {

    private static Pet createTestData() {
        Pet data = new Pet();
        data.setId("p1");
        data.setName("POCHI");
        data.setUser(User.builder().id("1").build());
//        data.setTags(Sets.newHashSet(new Tag("Docs"), new Tag("In the Rooms")));
        data.setColor(new Color("White"));
        data.setType(new Type("T.Poodle"));
        data.setBlood(new Blood("DEA1.1"));
        data.setProfile("Profile Details");
        data.setAllergia("Allergia Description");
        data.setDrug("Drug Description");
        data.setOther("Other Description");
        data.setSex(SexType.MALE);
        data.setBirthDate(LocalDateTime.of(2015, 2, 27, 15, 0, 0));
        data.setMicrochipDate(LocalDateTime.of(2015, 2, 27, 15, 0, 0));
        data.setMicrochipNo("1234567890");
        return data;
    }

    private static Map<String, Object> createProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PetzUserKey.LANG, LangKey.JAPANESE);
        properties.put(PetzUserKey.TIMEZONE, TimeZone.ASIA_TOKYO);
        return properties;
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetAllTest {

        private MockMvc mockMvc;

        @Mocked
        private PetService petService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            PetController sut = new PetController();
            sut.setPetService(petService);
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
        }

        @Test
        public void ペットが検索できること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getPrincipal();
                result = Optional.of(new PetzUser("userId", "username", "password", createProperties(), Collections.<GrantedAuthority>emptyList()));
                final Pageable pageable = PaginationUtils.generatePageRequest(1, 1);
                petService.getPetsByPetCriteria(new PetCriteria(), pageable);
                result = new PageImpl<>(Arrays.asList(Pet.builder().id("p1").name("test data").profile("test data's profile")
                        .birthDate(LocalDateTime.of(2015, 2, 27, 15, 0)).sex(SexType.MALE)
                        .type(new Type("type1"))
//                        .tags(Sets.newHashSet(new Tag("tag1"), new Tag("tag2")))
                        .user(User.builder().id("u1").build())
                        .build()), pageable, 2);
            }};

            mockMvc.perform(get("/api/v1/pets")
                    .param("page", "1")
                    .param("per_page", "1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].id", is("p1")))
                    .andExpect(jsonPath("$.[0].name", is("test data")))
                    .andExpect(jsonPath("$.[0].user.id", is("u1")))
                    .andExpect(jsonPath("$.[0].birthDate", is("2015-02-28T00:00:00+09:00")))
                    .andExpect(jsonPath("$.[0].sex", is("MALE")))
                    .andExpect(jsonPath("$.[0].profile", is("test data's profile")))
                    .andExpect(jsonPath("$.[0].type", is("type1")))
                    .andExpect(jsonPath("$.[0].tags[0]", is("tag1")))
                    .andExpect(jsonPath("$.[0].tags[1]", is("tag2")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class ShowTest {

        private MockMvc mockMvc;

        @Mocked
        private PetService petService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            PetController sut = new PetController();
            sut.setPetService(petService);
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
        }

        @Test
        public void ログインユーザのペットが取得できること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getPrincipal();
                result = Optional.of(new PetzUser("userId", "username", "password", createProperties(), Collections.<GrantedAuthority>emptyList()));
                petService.findPetByPetId("p1");
                result = Pet.builder().id("p1").name("test data").profile("test data's profile")
                        .birthDate(LocalDateTime.of(2015, 2, 27, 15, 0)).sex(SexType.MALE)
                        .type(new Type("type1"))
//                        .tags(Sets.newHashSet(new Tag("tag1"), new Tag("tag2")))
                        .user(User.builder().id("u1").build())
                        .build();
            }};

            mockMvc.perform(get("/api/v1/pets/p1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("p1")))
                    .andExpect(jsonPath("$.name", is("test data")))
                    .andExpect(jsonPath("$.user.id", is("u1")))
                    .andExpect(jsonPath("$.birthDate", is("2015-02-28T00:00:00+09:00")))
                    .andExpect(jsonPath("$.sex", is("MALE")))
                    .andExpect(jsonPath("$.profile", is("test data's profile")))
                    .andExpect(jsonPath("$.type", is("type1")))
                    .andExpect(jsonPath("$.tags[0]", is("tag1")))
                    .andExpect(jsonPath("$.tags[1]", is("tag2")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PostTest {

        private MockMvc mockMvc;

        @Mocked
        private PetService petService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            PetController sut = new PetController();
            sut.setPetService(petService);
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
        }

        @Test
        public void ペットが登録されること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getPrincipal();
                result = Optional.of(new PetzUser("1", "username", "password", createProperties(), Collections.<GrantedAuthority>emptyList()));
                SecurityUtils.getCurrentUserId();
                result = "1";
                petService.savePet((Pet) any);
                result = createTestData();
            }};

            mockMvc.perform(post("/api/v1/pets")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content("{\"birthDate\":\"2015-02-27T15:00:00.000+09:00\",\"microchipDate\":\"2015-02-27T15:00:00.000+09:00\",\"microchipNo\":\"1234567890\"," +
                            "\"user\":{\"id\":\"1\"},\"type\":\"トイプードル\",\"color\":\"ホワイト\",\"blood\":\"DEA1.1\"," +
                            "\"sex\":\"MALE\",\"tags\":[\"血統書付き\",\"室内犬\"],\"neutral\":\"true\"," +
                            "\"name\":\"ポチ\",\"profile\":\"プロファイル\",\"allergia\":\"アレルギー\",\"drug\":\"薬剤\",\"other\":\"その他\"}"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is("p1")))
                    .andExpect(jsonPath("$.name", is("POCHI")))
                    .andExpect(jsonPath("$.user.id", is("1")))
                    .andExpect(jsonPath("$.tags[0]", is("Docs")))
                    .andExpect(jsonPath("$.tags[1]", is("In the Rooms")))
                    .andExpect(jsonPath("$.color", is("White")))
                    .andExpect(jsonPath("$.type", is("T.Poodle")))
                    .andExpect(jsonPath("$.blood", is("DEA1.1")))
                    .andExpect(jsonPath("$.profile", is("Profile Details")))
                    .andExpect(jsonPath("$.allergia", is("Allergia Description")))
                    .andExpect(jsonPath("$.drug", is("Drug Description")))
                    .andExpect(jsonPath("$.other", is("Other Description")))
                    .andExpect(jsonPath("$.sex", is("MALE")))
                    .andExpect(jsonPath("$.birthDate", is("2015-02-28T00:00:00+09:00")))
                    .andExpect(jsonPath("$.microchipDate", is("2015-02-28T00:00:00+09:00")))
                    .andExpect(jsonPath("$.microchipNo", is("1234567890")));
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class PutTest {

        private MockMvc mockMvc;

        @Mocked
        private PetService petService;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            PetController sut = new PetController();
            sut.setPetService(petService);
            mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
        }

        @Test
        public void ペットが更新されること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getPrincipal();
                result = Optional.of(new PetzUser("1", "username", "password", createProperties(), Collections.<GrantedAuthority>emptyList()));
                SecurityUtils.getCurrentUserId();
                result = "1";
                petService.savePet((Pet) any);
                result = createTestData();
            }};

            mockMvc.perform(put("/api/v1/pets/p1")
                    .contentType(TestUtils.APPLICATION_JSON_UTF8)
                    .content("{\"birthDate\":\"2015-02-27T15:00:00.000Z\",\"microchipDate\":\"2015-02-27T15:00:00.000Z\",\"microchipNo\":\"1234567890\"," +
                            "\"user\":{\"id\":\"1\"},\"type\":\"トイプードル\",\"color\":\"ホワイト\",\"blood\":\"DEA1.1\"," +
                            "\"sex\":\"MALE\",\"tags\":[\"血統書付き\",\"室内犬\"],\"neutral\":\"true\"," +
                            "\"name\":\"ポチ\",\"profile\":\"プロファイル\",\"allergia\":\"アレルギー\",\"drug\":\"薬剤\",\"other\":\"その他\"}"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is("p1")))
                    .andExpect(jsonPath("$.name", is("POCHI")))
                    .andExpect(jsonPath("$.user.id", is("1")))
                    .andExpect(jsonPath("$.tags[0]", is("Docs")))
                    .andExpect(jsonPath("$.tags[1]", is("In the Rooms")))
                    .andExpect(jsonPath("$.color", is("White")))
                    .andExpect(jsonPath("$.type", is("T.Poodle")))
                    .andExpect(jsonPath("$.blood", is("DEA1.1")))
                    .andExpect(jsonPath("$.profile", is("Profile Details")))
                    .andExpect(jsonPath("$.allergia", is("Allergia Description")))
                    .andExpect(jsonPath("$.drug", is("Drug Description")))
                    .andExpect(jsonPath("$.other", is("Other Description")))
                    .andExpect(jsonPath("$.sex", is("MALE")))
                    .andExpect(jsonPath("$.birthDate", is("2015-02-28T00:00:00+09:00")))
                    .andExpect(jsonPath("$.microchipDate", is("2015-02-28T00:00:00+09:00")))
                    .andExpect(jsonPath("$.microchipNo", is("1234567890")));
        }
    }
}
