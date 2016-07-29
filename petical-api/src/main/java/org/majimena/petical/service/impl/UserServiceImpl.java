package org.majimena.petical.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.common.exceptions.SystemException;
import org.majimena.petical.common.factory.BeanFactory;
import org.majimena.petical.common.utils.BeanFactoryUtils;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.common.utils.RandomUtils;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.datetime.L10nDateTimeProvider;
import org.majimena.petical.domain.Authority;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.domain.user.ActivationRegistry;
import org.majimena.petical.domain.user.PasswordRegistry;
import org.majimena.petical.domain.user.SignupRegistry;
import org.majimena.petical.domain.user.UserCriteria;
import org.majimena.petical.domain.user.UserOutline;
import org.majimena.petical.mails.UserEmailService;
import org.majimena.petical.repository.AuthorityRepository;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
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

    @Inject
    private UserEmailService userEmailService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Optional<User> requestPasswordReset(String login) {
        return userRepository.findOneByActivatedIsTrueAndLogin(login)
                .map(user -> {
                    // パスワードをリセットするキーを保存
                    user.setResetKey(RandomUtils.generateResetKey());
                    user.setResetDate(L10nDateTimeProvider.now().toLocalDateTime());
                    userRepository.save(user);
                    // パスワードをリセットするための確認メールを送信
                    userEmailService.sendPasswordResetMail(user);
                    return user;
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Optional<User> resetPassword(String password, String key) {
        return userRepository.findOneByResetKey(key)
                .filter(user -> {
                    // パスワードリセットキーは一時間のみ有効（過ぎたら使えない）
                    ZonedDateTime oneDayAgo = L10nDateTimeProvider.now().minusHours(1);
                    return user.getResetDate().isAfter(oneDayAgo.toLocalDateTime());
                })
                .map(user -> {
                    // 新しいパスワードを設定する
                    user.setActivated(true);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setResetKey(null);
                    user.setResetDate(null);
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
        Optional<User> user = userRepository.findOneByActivatedIsTrueAndLogin(criteria.getEmail());
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
        return userRepository.findOneByActivatedIsTrueAndLogin(loginId);
    }

    @Override
    public void signup(SignupRegistry registry) {
        // もしアクティベートされていないユーザーがいたら、間違えてもう一度ユーザー登録していると思われるので削除する
        userRepository.findOneByLogin(registry.getEmail())
                .filter(user -> !user.getActivated())
                .ifPresent(user -> {
                    userRepository.delete(user);
                    userRepository.flush();
                });

        // ロールを作成する
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);

        // 基本ユーザーを登録する（アクティベート前なのでログインできないユーザー）
        User user = new User();
        user.setUsername("Not Activated User");
        user.setEmail(registry.getEmail());
        user.setLogin(registry.getEmail());
        user.setPassword("not-activated-user-password");
        user.setCountry("JP");
        user.setLangKey(LangKey.JAPANESE);
        user.setTimeZone(TimeZone.ASIA_TOKYO);
        user.setActivated(false);
        user.setActivationKey(RandomUtils.generateActivationKey());
        user.setAuthorities(authorities);
        User saved = userRepository.save(user);

        // 登録メールアドレスに確認メールを送る
        userEmailService.sendActivationMail(saved);
    }

    @Override
    public Optional<User> activate(ActivationRegistry registry) {
        return userRepository.findOneByActivationKey(registry.getActivationKey())
                .map(user -> {
                    // パスワードを設定してアクティベートする
                    String password = passwordEncoder.encode(registry.getPassword());
                    user.setPassword(password);
                    user.setUsername(registry.getUsername());
                    user.setActivated(Boolean.TRUE);
                    user.setActivationKey(null);
                    return userRepository.save(user);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User activate(User user) {
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
    @Deprecated
    public Optional<User> activateRegistration(String key) {
        return userRepository.findOneByActivationKey(key)
                .map(user -> {
                    user.setActivated(true);
                    user.setActivationKey(null);
                    return userRepository.save(user);
                });
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
        userRepository.findOneByActivatedIsTrueAndLogin(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }
}
