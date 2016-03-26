package org.majimena.petical.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.majimena.petical.Application;
import org.majimena.petical.domain.User;
import org.majimena.petical.repository.UserRepository;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by todoken on 2015/07/06.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RemoveNotActivatedUsersSchedulerTest {

    @Inject
    private RemoveNotActivatedUsersScheduler sut;

    @Inject
    private UserRepository userRepository;

    @Test
    public void testFindNotActivatedUsersByCreationDateBefore() {
        sut.removeNotActivatedUsers();

        LocalDateTime now = LocalDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        assertThat(users).isEmpty();
    }
}
