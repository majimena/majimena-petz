package org.majimena.petz.service.impl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.common.exceptions.SystemException;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.UserContact;
import org.majimena.petz.domain.user.PasswordRegistry;
import org.majimena.petz.repository.AbstractSpringDBUnitTest;
import org.majimena.petz.service.UserService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by todoken on 2015/08/02.
 */
@RunWith(Enclosed.class)
public class UserServiceImplIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class GetUserByUserIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private UserService sut;

        @Test
        @DatabaseSetup("classpath:/testdata/user.xml")
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
        @DatabaseSetup("classpath:/testdata/user.xml")
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
        @DatabaseSetup("classpath:/testdata/user.xml")
        public void ユーザーが更新できること() throws Exception {
            User r = User.builder().id("1").firstName("FirstName").lastName("LastName").email("todoken@example.com").build();

            User result = sut.patchUser(r);

            assertThat(result.getId(), is("1"));
            assertThat(result.getLogin(), is("hoge@hoge.com"));
            assertThat(result.getPassword(), is("password"));
            assertThat(result.getFirstName(), is("FirstName"));
            assertThat(result.getLastName(), is("LastName"));
            assertThat(result.getEmail(), is("todoken@example.com"));
            assertThat(result.getActivated(), is(Boolean.FALSE));
        }

        @Test(expected = SystemException.class)
        @DatabaseSetup("classpath:/testdata/user.xml")
        public void ユーザーが存在しない場合はシステム例外が発生すること() throws Exception {
            User r = User.builder().id("999").firstName("FirstName").lastName("LastName").email("todoken@example.com").build();

            sut.patchUser(r);
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class GetUserContactByUserIdTest extends AbstractSpringDBUnitTest {

        @Inject
        private UserService sut;

        @Test
        @DatabaseSetup("classpath:/testdata/user_contact.xml")
        public void ユーザー連絡先が取得できること() throws Exception {
            Optional<UserContact> contact = sut.getUserContactByUserId("1");

            UserContact result = contact.get();
            assertThat(result.getId(), is("1"));
            assertThat(result.getCountry(), is("JP"));
            assertThat(result.getZipCode(), is("1230000"));
            assertThat(result.getState(), is("東京都"));
            assertThat(result.getCity(), is("新宿区"));
            assertThat(result.getStreet(), is("ペット町１丁目１番地１号　ペッツハイツ１００"));
            assertThat(result.getPhoneNo(), is("0311110000"));
            assertThat(result.getMobilePhoneNo(), is("09011112222"));
        }

        @Test
        @DatabaseSetup("classpath:/testdata/user_contact.xml")
        public void ユーザー連絡先が存在しない場合は何も取得できないこと() throws Exception {
            Optional<UserContact> result = sut.getUserContactByUserId("999");

            assertThat(result.isPresent(), is(false));
        }
    }

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class SaveUserContactTest extends AbstractSpringDBUnitTest {

        @Inject
        private UserService sut;

        @Test
        @DatabaseSetup("classpath:/testdata/user_contact.xml")
        public void データが存在しない場合はユーザー連絡先が保存できること() throws Exception {
            Optional<UserContact> exists = sut.getUserContactByUserId("9");
            assertThat(exists.isPresent(), is(false));

            UserContact c = new UserContact("9", null, "1110000", "東京都", "新宿区", "ペット町１丁目１番地１号　ペッツハイツ１１１", "0311110000", "09011112222");
            UserContact result = sut.saveUserContact(c);

            assertThat(result.getId(), is("9"));
            assertThat(result.getCountry(), is("JP"));
            assertThat(result.getZipCode(), is("1110000"));
            assertThat(result.getState(), is("東京都"));
            assertThat(result.getCity(), is("新宿区"));
            assertThat(result.getStreet(), is("ペット町１丁目１番地１号　ペッツハイツ１１１"));
            assertThat(result.getPhoneNo(), is("0311110000"));
            assertThat(result.getMobilePhoneNo(), is("09011112222"));
        }

        @Test
        @DatabaseSetup("classpath:/testdata/user_contact.xml")
        public void データが存在する場合はユーザー連絡先が更新できること() throws Exception {
            Optional<UserContact> exists = sut.getUserContactByUserId("1");
            UserContact result = exists.get();
            assertThat(result.getId(), is("1"));
            assertThat(result.getCountry(), is("JP"));
            assertThat(result.getZipCode(), is("1230000"));
            assertThat(result.getState(), is("東京都"));
            assertThat(result.getCity(), is("新宿区"));
            assertThat(result.getStreet(), is("ペット町１丁目１番地１号　ペッツハイツ１００"));
            assertThat(result.getPhoneNo(), is("0311110000"));
            assertThat(result.getMobilePhoneNo(), is("09011112222"));

            UserContact c = new UserContact("1", null, "1110000", "東京都", "新宿区", "ペット町１丁目１番地１号　ペッツハイツ１１１", "0311110000", "09011112222");
            result = sut.saveUserContact(c);

            assertThat(result.getId(), is("1"));
            assertThat(result.getCountry(), is("JP"));
            assertThat(result.getZipCode(), is("1110000"));
            assertThat(result.getState(), is("東京都"));
            assertThat(result.getCity(), is("新宿区"));
            assertThat(result.getStreet(), is("ペット町１丁目１番地１号　ペッツハイツ１１１"));
            assertThat(result.getPhoneNo(), is("0311110000"));
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
        @DatabaseSetup("classpath:/testdata/user.xml")
        public void パスワードが変更できること() throws Exception {
            sut.changePassword(new PasswordRegistry("3", "admin", "drowssap"));

            Optional<User> contact = sut.getUserByUserId("3");
            User result = contact.get();
            assertThat(result.getId(), is("3"));
            assertThat(encoder.matches("drowssap", result.getPassword()), is(true));
        }

        @Test(expected = ApplicationException.class)
        @DatabaseSetup("classpath:/testdata/user.xml")
        public void パスワードが異なる場合はアプリ例外が発生すること() throws Exception {
            sut.changePassword(new PasswordRegistry("3", "password", "drowssap"));
        }

        @Test(expected = SystemException.class)
        @DatabaseSetup("classpath:/testdata/user.xml")
        public void ユーザーが存在しない場合はシステム例外が発生すること() throws Exception {
            sut.changePassword(new PasswordRegistry("999", "admin", "drowssap"));
        }
    }
}
