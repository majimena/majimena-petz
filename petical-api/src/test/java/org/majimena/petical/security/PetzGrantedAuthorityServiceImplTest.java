package org.majimena.petical.security;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.ClinicStaff;
import org.majimena.petical.domain.authentication.PetzGrantedAuthority;
import org.majimena.petical.repository.ClinicStaffRepository;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see PetzUserDetailsServiceImpl
 */
@RunWith(Enclosed.class)
public class PetzGrantedAuthorityServiceImplTest {

    @Tested
    private PetzGrantedAuthorityServiceImpl sut = new PetzGrantedAuthorityServiceImpl();
    @Injectable
    private ClinicStaffRepository clinicStaffRepository;

    @Test
    public void 権限情報が取得できること() throws Exception {
        new NonStrictExpectations() {{
            clinicStaffRepository.findByUserId("1");
            result = Arrays.asList(ClinicStaff.builder().id("1").role("ROLE_USER").clinic(Clinic.builder().id("clinic1").build()).build());
        }};

        List<PetzGrantedAuthority> result = sut.getAuthoritiesByUserId("1");

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getClinicId(), is("clinic1"));
        assertThat(result.get(0).getRole(), is("ROLE_USER"));
    }
}
