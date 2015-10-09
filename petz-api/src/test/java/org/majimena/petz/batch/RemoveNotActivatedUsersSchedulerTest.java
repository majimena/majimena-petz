package org.majimena.petz.batch;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.batch.RemoveNotActivatedUsersScheduler;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.UserRepository;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
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

        DateTime now = new DateTime();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        assertThat(users).isEmpty();
    }
}
