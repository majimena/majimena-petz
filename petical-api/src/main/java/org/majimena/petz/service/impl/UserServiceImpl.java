package org.majimena.petz.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.common.exceptions.SystemException;
import org.majimena.petz.common.factory.BeanFactory;
import org.majimena.petz.common.utils.BeanFactoryUtils;
import org.majimena.petz.common.utils.ExceptionUtils;
import org.majimena.petz.common.utils.RandomUtils;
import org.majimena.petz.datatype.LangKey;
import org.majimena.petz.datatype.TimeZone;
import org.majimena.petz.domain.Authority;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.domain.user.PasswordRegistry;
import org.majimena.petz.domain.user.SignupRegistry;
import org.majimena.petz.domain.user.UserCriteria;
import org.majimena.petz.domain.user.UserOutline;
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
import java.time.LocalDateTime;
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
                    LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(24);
                    return user.getResetDate().isAfter(oneDayAgo);
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
                    user.setResetDate(LocalDateTime.now());
                    userRepository.save(user);
                    return user;
                });
    }

    @Override
    public User createUserInformation(String login, String password, String firstName, String lastName, String email,
                                      LangKey langKey) {

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
        Optional<User> user = userRepository.findOneByLogin(criteria.getEmail());
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
    public Optional<User> getUserByLogin(String loginId) {
        return userRepository.findOneByLogin(loginId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUser(SignupRegistry registry) {
        // ロールを作成する
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);

        // パスワードのハッシュ化
        String encryptedPassword = passwordEncoder.encode(registry.getPassword());

        // ユーザーを登録する
        User newUser = new User();
        newUser.setFirstName(registry.getFirstName());
        newUser.setLastName(registry.getLastName());
        newUser.setUsername(registry.getFirstName());
        newUser.setEmail(registry.getEmail());
        newUser.setLogin(registry.getEmail());
        newUser.setPassword(encryptedPassword);
        // TODO 海外対応を考える
        newUser.setLangKey(LangKey.JAPANESE);
        newUser.setTimeZone(TimeZone.ASIA_TOKYO);
        newUser.setCountry("JP");

        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtils.generateActivationKey());
        newUser.setAuthorities(authorities);
        return userRepository.save(newUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUser(User user) {
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);

        // TODO メール送信したらパスワードをランダムに発行してそこに記載する
        String encryptedPassword;
        if (StringUtils.isEmpty(user.getPassword())) {
            encryptedPassword = passwordEncoder.encode("password");
        } else {
            encryptedPassword = passwordEncoder.encode(user.getPassword());
        }
        user.setPassword(encryptedPassword);

        user.setLangKey(LangKey.JAPANESE); // FIXME 他言語対応
        user.setTimeZone(TimeZone.ASIA_TOKYO);
        user.setCountry("JP"); // FIXME 海外対応
        user.setActivated(false);
        user.setActivationKey(RandomUtils.generateActivationKey());
        user.setAuthorities(authorities);
        User created = userRepository.save(user);

        return created;
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

    @Override
    public User updateUser(User user) {
        User one = userRepository.findOne(user.getId());
        ExceptionUtils.throwIfNull(one);

        BeanFactoryUtils.copyNonNullProperties(user, one);
        return userRepository.save(one);
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
