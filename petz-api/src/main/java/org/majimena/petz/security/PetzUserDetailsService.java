package org.majimena.petz.security;

import org.majimena.petz.domain.User;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ユーザー認証サービス.
 */
@Component
@Transactional
public class PetzUserDetailsService implements UserDetailsService {

    /**
     * ユーザーリポジトリ.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * クリニックスタッフリポジトリ.
     */
    @Inject
    private ClinicStaffRepository clinicStaffRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(final String login) {
        return userRepository.findOneByLogin(login)
                .map(user -> toPetzUser(user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));
    }

    /**
     * 認証ユーザー情報に変換する.
     *
     * @param user 登録ユーザー情報
     * @return 認証ユーザー情報
     */
    protected PetzUser toPetzUser(User user) {
        // ユーザー権限
        List<GrantedAuthority> authorities = user.getAuthorities().stream()
                .map(authority -> new PetzGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());

        // クリニックスタッフ権限
        clinicStaffRepository.findByUserId(user.getId()).stream()
                .forEach(o -> authorities.add(new PetzGrantedAuthority(o.getClinic().getId(), o.getRole())));

        // ログインユーザー情報
        return new PetzUser(user.getId(), user.getLogin(), user.getPassword(), authorities);
    }
}
