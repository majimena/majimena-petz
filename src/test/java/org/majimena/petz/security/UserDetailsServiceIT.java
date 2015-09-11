package org.majimena.petz.security;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.Application;
import org.majimena.petz.repository.AbstractSpringDBUnitTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by todoken on 2015/09/11.
 */
@RunWith(Enclosed.class)
public class UserDetailsServiceIT {

    @WebAppConfiguration
    @SpringApplicationConfiguration(classes = Application.class)
    public static class LoadUserByUsernameTest extends AbstractSpringDBUnitTest {

        @Inject
        private UserDetailsService sut;

        @Test
        @DatabaseSetup("classpath:/testdata/security.xml")
        public void 認証できること() throws Exception {
            UserDetails result = sut.loadUserByUsername("login1");
            GrantedAuthority[] authorities = result.getAuthorities().toArray(new GrantedAuthority[]{});

            assertThat(result.getUsername(), is("login1"));
            assertThat(result.getPassword(), is("password1"));
            assertThat(authorities.length, is(3));
            assertThat(authorities[0].getAuthority(), is("ROLE_CLINIC_ADMIN-1"));
            assertThat(authorities[1].getAuthority(), is("ROLE_CLINIC_ADMIN-2"));
            assertThat(authorities[2].getAuthority(), is("ROLE_USER"));
        }

        @Test(expected = UsernameNotFoundException.class)
        @DatabaseSetup("classpath:/testdata/security.xml")
        public void 認証できないこと() throws Exception {
            sut.loadUserByUsername("login9");
        }
    }
}
