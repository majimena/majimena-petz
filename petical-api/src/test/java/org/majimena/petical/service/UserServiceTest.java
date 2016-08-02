package org.majimena.petical.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.majimena.petical.WebApplication;
import org.majimena.petical.common.utils.RandomUtils;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.domain.User;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.service.impl.UserServiceImpl;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserServiceImpl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class UserServiceTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserServiceImpl userService;

    @Ignore
    @Test
    public void assertThatUserMustExistToResetPassword() {

        Optional<User> maybeUser = userService.requestPasswordReset("john.doe@localhost");
        assertThat(maybeUser.isPresent()).isFalse();

        maybeUser = userService.requestPasswordReset("admin@localhost");
        assertThat(maybeUser.isPresent()).isTrue();

        assertThat(maybeUser.get().getEmail()).isEqualTo("admin@localhost");
        assertThat(maybeUser.get().getResetDate()).isNotNull();
        assertThat(maybeUser.get().getResetKey()).isNotNull();

    }

    @Ignore
    @Test
    public void assertThatResetKeyMustNotBeOlderThan24Hours() {

        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost", LangKey.ENGLISH);

        LocalDateTime daysAgo = LocalDateTime.now().minusHours(25);
        String resetKey = RandomUtils.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);

        userRepository.save(user);

        Optional<User> maybeUser = userService.resetPassword("johndoe2", user.getResetKey());

        assertThat(maybeUser.isPresent()).isFalse();

        userRepository.delete(user);

    }

    @Ignore
    @Test
    public void assertThatResetKeyMustBeValid() {

        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost", LangKey.ENGLISH);

        LocalDateTime daysAgo = LocalDateTime.now().minusHours(25);
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey("1234");

        userRepository.save(user);

        Optional<User> maybeUser = userService.resetPassword("johndoe2", user.getResetKey());

        assertThat(maybeUser.isPresent()).isFalse();

        userRepository.delete(user);

    }

    @Ignore
    @Test
    public void assertThatUserCanResetPassword() {

        User user = userService.createUserInformation("johndoe", "johndoe", "John", "Doe", "john.doe@localhost", LangKey.ENGLISH);

        String oldPassword = user.getPassword();

        LocalDateTime daysAgo = LocalDateTime.now().minusHours(2);
        String resetKey = RandomUtils.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);

        userRepository.save(user);

        Optional<User> maybeUser = userService.resetPassword("johndoe2", user.getResetKey());

        assertThat(maybeUser.isPresent()).isTrue();
        assertThat(maybeUser.get().getResetDate()).isNull();
        assertThat(maybeUser.get().getResetKey()).isNull();
        assertThat(maybeUser.get().getPassword()).isNotEqualTo(oldPassword);

        userRepository.delete(user);

    }
}
