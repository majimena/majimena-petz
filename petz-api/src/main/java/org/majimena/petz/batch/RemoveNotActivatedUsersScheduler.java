package org.majimena.petz.batch;

import org.majimena.petz.domain.User;
import org.majimena.petz.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by todoken on 2015/07/06.
 */
@Component
public class RemoveNotActivatedUsersScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveNotActivatedUsersScheduler.class);

    @Inject
    protected UserRepository userRepository;

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        LocalDateTime now = LocalDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3L));
        for (User user : users) {
            LOGGER.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }
}
