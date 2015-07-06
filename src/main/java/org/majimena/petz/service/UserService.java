package org.majimena.petz.service;

import org.majimena.petz.domain.User;

import java.util.Optional;

/**
 * Created by todoken on 2015/07/05.
 */
public interface UserService {

    Optional<User> activateRegistration(String key);

    Optional<User> completePasswordReset(String newPassword, String key);

    Optional<User> requestPasswordReset(String mail);

    User createUserInformation(String login, String password, String firstName, String lastName, String email, String langKey);

    void updateUserInformation(String firstName, String lastName, String email);

    void changePassword(String password);

    User getUserWithAuthorities();

}
