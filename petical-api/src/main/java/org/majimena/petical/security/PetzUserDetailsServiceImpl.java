package org.majimena.petical.security;

import org.majimena.petical.security.authentication.PetzGrantedAuthority;
import org.majimena.petical.security.authentication.PetzUser;
import org.majimena.petical.security.authentication.PetzUserKey;
import org.majimena.petical.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ユーザー認証サービス.
 */
@Component
@Transactional
public class PetzUserDetailsServiceImpl implements UserDetailsService {

    /**
     * ユーザーリポジトリ.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(final String login) {
        return userRepository.findOneByActivatedIsTrueAndLogin(login)
                .map(user -> {
                    // ユーザー権限
                    List<GrantedAuthority> authorities = user.getAuthorities().stream()
                            .map(authority -> new PetzGrantedAuthority(authority.getName()))
                            .collect(Collectors.toList());

                    // ログインユーザー情報
                    Map<String, Object> properties = new HashMap<>();
                    properties.put(PetzUserKey.LANG, user.getLangKey());
                    properties.put(PetzUserKey.TIMEZONE, user.getTimeZone());
                    return new PetzUser(user.getId(), user.getLogin(), user.getPassword(), properties, authorities);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));
    }
}
