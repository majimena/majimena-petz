package org.majimena.petz.security;

import com.google.common.collect.Sets;
import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.domain.Authority;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see PetzUserDetailsServiceImpl
 */
@RunWith(Enclosed.class)
public class PetzUserDetailsServiceImplTest {

    @Tested
    private PetzUserDetailsServiceImpl sut = new PetzUserDetailsServiceImpl();
    @Injectable
    private UserRepository userRepository;

    private static User createUser() {
        return User.builder()
                .id("1")
                .username("Test")
                .login("login1")
                .password("password1")
                .authorities(Sets.newHashSet(new Authority("ROLE_USER"), new Authority(""), new Authority("")))
                .build();
    }

    @Test
    public void 認証できること() throws Exception {
        new NonStrictExpectations() {{
            userRepository.findOneByLogin("login1");
            result = Optional.of(createUser());
        }};

        UserDetails result = sut.loadUserByUsername("login1");
        GrantedAuthority[] authorities = result.getAuthorities().toArray(new GrantedAuthority[]{});

        assertThat(result.getUsername(), is("login1"));
        assertThat(result.getPassword(), is("password1"));
        assertThat(authorities.length, is(1));
        assertThat(authorities[0].getAuthority(), is("ROLE_USER"));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void 認証できないこと() throws Exception {
        new NonStrictExpectations() {{
            userRepository.findOneByLogin("login9");
            result = Optional.empty();
        }};

        sut.loadUserByUsername("login9");
    }
}
