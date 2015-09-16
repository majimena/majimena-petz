package org.majimena.petz.service.impl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.domain.Color;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Tag;
import org.majimena.petz.domain.Type;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.common.SexType;
import org.majimena.petz.repository.AbstractSpringDBUnitTest;
import org.majimena.petz.service.PetService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @see PetServiceImpl
 */
@RunWith(Enclosed.class)
public class PetServiceImplIT {

    @Transactional
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class FindPetsByUserIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private PetService sut;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void ユーザーのペット一覧が取得できること() throws Exception {
            List<Pet> result = sut.findPetsByUserId("1");

            assertThat(result.size(), is(1));
            assertThat(result.get(0).getId(), is("1"));
            assertThat(result.get(0).getName(), is("ハチ"));
            assertThat(result.get(0).getProfile(), is("渋谷ハチ公"));
            assertThat(result.get(0).getBirthDate(), is(LocalDateTime.of(2000, 1, 1, 0, 0)));
            assertThat(result.get(0).getSex(), is(SexType.MALE));
            assertThat(result.get(0).getType(), is(new Type("柴犬")));
            assertThat(result.get(0).getTags().size(), is(1));
            assertThat(result.get(0).getTags().contains(new Tag("忠犬")), is(true));
        }

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void ユーザーが存在しない場合は何も取得できないこと() throws Exception {
            List<Pet> result = sut.findPetsByUserId("999");
            assertThat(result.size(), is(0));
        }
    }

    @Transactional
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class FindPetByPetIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private PetService sut;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void 該当するペットが取得できること() throws Exception {
            Pet result = sut.findPetByPetId("1");

            assertThat(result.getId(), is("1"));
            assertThat(result.getName(), is("ハチ"));
            assertThat(result.getProfile(), is("渋谷ハチ公"));
            assertThat(result.getBirthDate(), is(LocalDateTime.of(2000, 1, 1, 0, 0)));
            assertThat(result.getSex(), is(SexType.MALE));
            assertThat(result.getType(), is(new Type("柴犬")));
            assertThat(result.getTags().size(), is(1));
            assertThat(result.getTags().contains(new Tag("忠犬")), is(true));
        }

        @Test(expected = ResourceNotFoundException.class)
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void 該当するペットがいない場合は例外が発生すること() throws Exception {
            sut.findPetByPetId("999");
        }
    }

    @Transactional
    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class SavePetTest extends AbstractSpringDBUnitTest {

        @Inject
        private PetService sut;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void 全ての項目が入力されている場合にペットが保存できること() throws Exception {
            LocalDateTime now = LocalDateTime.now();
            final Pet testData = Pet.builder().name("ポチ").profile("プロファイル").birthDate(now).sex(SexType.MALE)
                    .user(User.builder().id("1").build())
                    .type(new Type("トイプードル")).color(new Color("ホワイト"))
                    .tags(Sets.newHashSet(new Tag("室内犬"), new Tag("血統書"))).build();

            Pet result = sut.savePet(testData);

            assertThat(result.getId(), is(notNullValue()));
            assertThat(result.getName(), is("ポチ"));
            assertThat(result.getProfile(), is("プロファイル"));
            assertThat(result.getUser().getId(), is("1"));
            assertThat(result.getUser().getLogin(), is("hoge@hoge.com"));
            assertThat(result.getBirthDate(), is(now));
            assertThat(result.getSex(), is(SexType.MALE));
            assertThat(result.getType(), is(new Type("トイプードル")));
            assertThat(result.getColor(), is(new Color("ホワイト")));
            assertThat(result.getTags().size(), is(2));
            assertThat(result.getTags().contains(new Tag("室内犬")), is(true));
            assertThat(result.getTags().contains(new Tag("血統書")), is(true));
        }

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void 任意項目が入力されていない場合にペットが保存できること() throws Exception {
            final Pet testData = Pet.builder().name("ポチ")
                    .user(User.builder().id("1").build())
                    .type(new Type("トイプードル")).color(new Color("ホワイト")).build();

            Pet result = sut.savePet(testData);

            assertThat(result.getId(), is(notNullValue()));
            assertThat(result.getName(), is("ポチ"));
            assertThat(result.getProfile(), is(nullValue()));
            assertThat(result.getUser().getId(), is("1"));
            assertThat(result.getUser().getLogin(), is("hoge@hoge.com"));
            assertThat(result.getBirthDate(), is(nullValue()));
            assertThat(result.getSex(), is(nullValue()));
            assertThat(result.getType(), is(new Type("トイプードル")));
            assertThat(result.getColor(), is(new Color("ホワイト")));
            assertThat(result.getTags(), is(nullValue()));
        }
    }
}
