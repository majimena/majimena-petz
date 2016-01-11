package org.majimena.petz.security;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.domain.Authority;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.majimena.petz.domain.User;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see PetzUserDetailsService
 */
@RunWith(Enclosed.class)
public class PetzUserDetailsServiceTest {

    public static class LoadUserByUsernameTest {

        @Tested
        private PetzUserDetailsService sut = new PetzUserDetailsService();
        @Injectable
        private UserRepository userRepository;
        @Injectable
        private ClinicStaffRepository clinicStaffRepository;

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
                clinicStaffRepository.findByUserId("1");
                result = Lists.newArrayList(
                        ClinicStaff.builder().id("clinic1").role("ROLE_CLINIC_OWNER").clinic(Clinic.builder().id("1").build()).build(),
                        ClinicStaff.builder().id("clinic2").role("ROLE_CLINIC_STAFF").clinic(Clinic.builder().id("2").build()).build());
            }};

            UserDetails result = sut.loadUserByUsername("login1");
            GrantedAuthority[] authorities = result.getAuthorities().toArray(new GrantedAuthority[]{});

            assertThat(result.getUsername(), is("login1"));
            assertThat(result.getPassword(), is("password1"));
            assertThat(authorities.length, is(3));
            assertThat(authorities[0].getAuthority(), is("ROLE_CLINIC_OWNER-1"));
            assertThat(authorities[1].getAuthority(), is("ROLE_CLINIC_STAFF-2"));
            assertThat(authorities[2].getAuthority(), is("ROLE_USER"));
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
}
