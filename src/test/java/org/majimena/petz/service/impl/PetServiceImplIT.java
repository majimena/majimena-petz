package org.majimena.petz.service.impl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.datatypes.SexType;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Tag;
import org.majimena.petz.domain.Type;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.AbstractSpringDBUnitTest;
import org.majimena.petz.service.PetService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by todoken on 2015/07/28.
 */
@RunWith(Enclosed.class)
public class PetServiceImplIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class FindPetsByUserIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private PetService sut;

        @Test
        @DatabaseSetup("classpath:/testdata/pet.xml")
        public void ユーザーのペット一覧が取得できること() throws Exception {
            List<Pet> result = sut.findPetsByUserId("1");

            assertThat(result.size(), is(1));
            assertThat(result.get(0).getId(), is("1"));
            assertThat(result.get(0).getName(), is("ハチ"));
            assertThat(result.get(0).getProfile(), is("渋谷ハチ公"));
            assertThat(result.get(0).getUser().getId(), is("1"));
            assertThat(result.get(0).getUser().getLogin(), is("login1"));
            assertThat(result.get(0).getBirthDate(), is(LocalDate.of(2000, 1, 1)));
            assertThat(result.get(0).getSex(), is(SexType.MALE));
            assertThat(result.get(0).getTypes().size(), is(1));
            assertThat(result.get(0).getTypes().contains(new Type("柴犬")), is(true));
            assertThat(result.get(0).getTags().size(), is(1));
            assertThat(result.get(0).getTags().contains(new Tag("忠犬")), is(true));
        }

        @Test
        @DatabaseSetup("classpath:/testdata/pet.xml")
        public void ユーザーが存在しない場合は何も取得できないこと() throws Exception {
            List<Pet> result = sut.findPetsByUserId("999");
            assertThat(result.size(), is(0));
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class FindPetByPetIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private PetService sut;

        @Test
        @DatabaseSetup("classpath:/testdata/pet.xml")
        public void 該当するペットが取得できること() throws Exception {
            Pet result = sut.findPetByPetId("1");

            assertThat(result.getId(), is("1"));
            assertThat(result.getName(), is("ハチ"));
            assertThat(result.getProfile(), is("渋谷ハチ公"));
            assertThat(result.getUser().getId(), is("1"));
            assertThat(result.getUser().getLogin(), is("login1"));
            assertThat(result.getBirthDate(), is(LocalDate.of(2000, 1, 1)));
            assertThat(result.getSex(), is(SexType.MALE));
            assertThat(result.getTypes().size(), is(1));
            assertThat(result.getTypes().contains(new Type("柴犬")), is(true));
            assertThat(result.getTags().size(), is(1));
            assertThat(result.getTags().contains(new Tag("忠犬")), is(true));
        }

        @Test(expected = ResourceNotFoundException.class)
        @DatabaseSetup("classpath:/testdata/pet.xml")
        public void 該当するペットがいない場合は例外が発生すること() throws Exception {
            sut.findPetByPetId("999");
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class SavePetTest extends AbstractSpringDBUnitTest {

        @Inject
        private PetService sut;

        @Test
        @Transactional
        @DatabaseSetup("classpath:/testdata/pet.xml")
        public void sampleTest() throws Exception {
            LocalDate now = LocalDate.now();
            final Pet testData = Pet.builder().name("ポチ").profile("プロファイル").birthDate(now).sex(SexType.MALE)
                .user(User.builder().id("1").build())
                .types(Sets.newHashSet(new Type("トイプードル"), new Type("マルチーズ")))
                .tags(Sets.newHashSet(new Tag("室内犬"), new Tag("血統書"))).build();

            Pet result = sut.savePet(testData);

            assertThat(result.getId(), is(notNullValue()));
            assertThat(result.getName(), is("ポチ"));
            assertThat(result.getProfile(), is("プロファイル"));
            assertThat(result.getUser().getId(), is("1"));
            assertThat(result.getUser().getLogin(), is("login1"));
            assertThat(result.getBirthDate(), is(now));
            assertThat(result.getSex(), is(SexType.MALE));
            assertThat(result.getTypes().size(), is(2));
            assertThat(result.getTypes().contains(new Type("トイプードル")), is(true));
            assertThat(result.getTypes().contains(new Type("マルチーズ")), is(true));
            assertThat(result.getTags().size(), is(2));
            assertThat(result.getTags().contains(new Tag("室内犬")), is(true));
            assertThat(result.getTags().contains(new Tag("血統書")), is(true));
        }
    }
}
