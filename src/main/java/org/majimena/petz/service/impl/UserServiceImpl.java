package org.majimena.petz.service.impl;

import org.joda.time.DateTime;
import org.majimena.framework.beans.factory.BeanFactory;
import org.majimena.framework.beans.utils.BeanFactoryUtils;
import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.common.exceptions.SystemException;
import org.majimena.petz.common.utils.RandomUtils;
import org.majimena.petz.domain.Authority;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.UserContact;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.domain.user.PasswordRegistry;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.domain.user.UserCriteria;
import org.majimena.petz.domain.user.UserOutline;
import org.majimena.petz.domain.user.UserPatchRegistry;
import org.majimena.petz.repository.AuthorityRepository;
import org.majimena.petz.repository.UserContactRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    private UserContactRepository userContactRepository;

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
    public List<UserOutline> getUsersByUserCriteria(final UserCriteria criteria) {
        // 今はメアド検索しかないのでこのままでもOK
        Optional<User> user = userRepository.findOneByEmail(criteria.getEmail());
        return user
                .map(u -> Arrays.asList(BeanFactory.create(u, new UserOutline())))
                .orElse(Arrays.asList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUserId(String userId) {
        User currentUser = userRepository.findOne(userId);
        return Optional.ofNullable(currentUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserContact> getUserContactByUserId(String userId) {
        UserContact contact = userContactRepository.findOne(userId);
        return Optional.ofNullable(contact);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public UserContact saveUserContact(UserContact contact) {
        contact.setCountry("JP");
        return userContactRepository.save(contact);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changePassword(PasswordRegistry registry) {
        User one = userRepository.findOne(registry.getUserId());
        if (one == null) {
            throw new SystemException("user not found [" + registry.getUserId() + "]");
        }

        // パスワードチェック
        if (!passwordEncoder.matches(registry.getOldPassword(), one.getPassword())) {
            throw new ApplicationException(ErrorCode.PTZ_000102);
        }

        String newPassword = passwordEncoder.encode(registry.getNewPassword());
        one.setPassword(newPassword);
        userRepository.save(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updateUser(UserPatchRegistry registry) {
        User one = userRepository.findOne(registry.getUserId());
        if (one == null) {
            throw new SystemException("user not found [" + registry.getUserId() + "]");
        }

        BeanFactoryUtils.copyNonNullProperties(registry, one);
        userRepository.save(one);
        return one;
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
}
