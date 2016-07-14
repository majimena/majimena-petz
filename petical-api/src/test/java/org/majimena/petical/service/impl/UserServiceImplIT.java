package org.majimena.petical.service.impl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petical.Application;
import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.common.exceptions.ResourceNotFoundException;
import org.majimena.petical.common.exceptions.SystemException;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.user.PasswordRegistry;
import org.majimena.petical.repository.AbstractSpringDBUnitTest;
import org.majimena.petical.service.UserService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @see UserServiceImpl
 */
@RunWith(Enclosed.class)
public class UserServiceImplIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class GetUserByUserIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private UserService sut;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void ユーザーが取得できること() throws Exception {
            Optional<User> user = sut.getUserByUserId("1");

            User result = user.get();
            assertThat(result.getId(), is("1"));
            assertThat(result.getLogin(), is("hoge@hoge.com"));
            assertThat(result.getPassword(), is("password"));
            assertThat(result.getEmail(), is("hoge@hoge.com"));
            assertThat(result.getActivated(), is(Boolean.FALSE));
        }

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void ユーザーが存在しない場合は何も取得できないこと() throws Exception {
            Optional<User> result = sut.getUserByUserId("999");

            assertThat(result.isPresent(), is(false));
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class PatchUserTest extends AbstractSpringDBUnitTest {

        @Inject
        private UserService sut;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void ユーザーが更新できること() throws Exception {
            User r = User.builder().id("1").firstName("FirstName").lastName("LastName").email("todoken@example.com").build();

            User result = sut.updateUser(r);

            assertThat(result.getId(), is("1"));
            assertThat(result.getLogin(), is("hoge@hoge.com"));
            assertThat(result.getPassword(), is("password"));
            assertThat(result.getFirstName(), is("FirstName"));
            assertThat(result.getLastName(), is("LastName"));
            assertThat(result.getEmail(), is("todoken@example.com"));
            assertThat(result.getActivated(), is(Boolean.FALSE));
        }

        @Test(expected = ResourceNotFoundException.class)
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void ユーザーが存在しない場合はシステム例外が発生すること() throws Exception {
            User r = User.builder().id("999").firstName("FirstName").lastName("LastName").email("todoken@example.com").build();

            sut.updateUser(r);
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class SaveUserTest extends AbstractSpringDBUnitTest {

        @Inject
        private UserService sut;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void ユーザー情報を保存できること() throws Exception {
            User user = new User();
            user.setUsername("テストデータ");
            user.setLogin("test@test.com");
            user.setPassword("password");
            user.setZipCode("1110000");
            user.setState("東京都");
            user.setCity("新宿区");
            user.setStreet("新宿１−１−１");
            user.setPhoneNo("0311112222");
            user.setMobilePhoneNo("09011112222");

            User result = sut.activate(user);

            assertThat(result.getId(), is(notNullValue()));
            assertThat(result.getUsername(), is("テストデータ"));
            assertThat(result.getLogin(), is("test@test.com"));
            assertThat(result.getPassword(), is(notNullValue()));
            assertThat(result.getLangKey(), is(LangKey.JAPANESE));
            assertThat(result.getTimeZone(), is(TimeZone.ASIA_TOKYO));
            assertThat(result.getCountry(), is("JP"));
            assertThat(result.getZipCode(), is("1110000"));
            assertThat(result.getState(), is("東京都"));
            assertThat(result.getCity(), is("新宿区"));
            assertThat(result.getStreet(), is("新宿１−１−１"));
            assertThat(result.getPhoneNo(), is("0311112222"));
            assertThat(result.getMobilePhoneNo(), is("09011112222"));
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class ChangePasswordTest extends AbstractSpringDBUnitTest {

        @Inject
        private UserService sut;

        @Inject
        private PasswordEncoder encoder;

        @Test
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void パスワードが変更できること() throws Exception {
            sut.changePassword(PasswordRegistry.builder().userId("3").oldPassword("admin").newPassword("drowssap").build());

            Optional<User> contact = sut.getUserByUserId("3");
            User result = contact.get();
            assertThat(result.getId(), is("3"));
            assertThat(encoder.matches("drowssap", result.getPassword()), is(true));
        }

        @Test(expected = ApplicationException.class)
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void パスワードが異なる場合はアプリ例外が発生すること() throws Exception {
            sut.changePassword(PasswordRegistry.builder().userId("3").oldPassword("admin").newPassword("drowssap").build());
        }

        @Test(expected = SystemException.class)
        @DatabaseSetup("classpath:/fixture/base.xml")
        public void ユーザーが存在しない場合はシステム例外が発生すること() throws Exception {
            sut.changePassword(PasswordRegistry.builder().userId("999").oldPassword("admin").newPassword("drowssap").build());
        }
    }
}
