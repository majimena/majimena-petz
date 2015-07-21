package org.majimena.petz.security;

import org.majimena.petz.domain.User;
import org.majimena.petz.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Inject
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        Optional<User> userFromDatabase = userRepository.findOneByLogin(login);
        userFromDatabase.orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));

        User user = userFromDatabase.get();
        // TODO アクティベートされていなくてもWARNINGで入れる？
//        if (!user.getActivated()) {
//            throw new UserNotActivatedException("User " + login + " was not activated");
//        }

        List<GrantedAuthority> authorities = user.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
            .collect(Collectors.toList());
        return new PetzUser(user.getId(), user.getLogin(), user.getPassword(), authorities);
    }
}
