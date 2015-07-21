package org.majimena.petz.service.impl;

import org.joda.time.DateTime;
import org.majimena.petz.common.utils.RandomUtils;
import org.majimena.petz.domain.Authority;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.repository.AuthorityRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Override
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
        return Optional.empty();
    }

    @Override
    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);

        return userRepository.findOneByResetKey(key)
            .filter(user -> {
                DateTime oneDayAgo = DateTime.now().minusHours(24);
                return user.getResetDate().isAfter(oneDayAgo.toInstant().getMillis());
            })
            .map(user -> {
                user.setActivated(true);
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                userRepository.save(user);
                return user;
            });
    }

    @Override
    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmail(mail)
            .map(user -> {
                user.setResetKey(RandomUtils.generateResetKey());
                user.setResetDate(DateTime.now());
                userRepository.save(user);
                return user;
            });
    }

    @Override
    public User createUserInformation(String login, String password, String firstName, String lastName, String email,
                                      String langKey) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtils.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveUser(SignupRegistry registry) {
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);

        String encryptedPassword = passwordEncoder.encode(registry.getPassword());

        User newUser = new User();
        newUser.setLogin(registry.getEmail());
        newUser.setPassword(encryptedPassword);
        newUser.setEmail(registry.getEmail());
        newUser.setLangKey("ja");
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtils.generateActivationKey());
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
    }

    @Override
    public void updateUserInformation(String firstName, String lastName, String email) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    @Override
    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        String login = SecurityUtils.getCurrentLogin();
        User currentUser = userRepository.findOneByLogin(login).get();
        currentUser.getAuthorities().size(); // eagerly load the association
        return currentUser;
    }
}
