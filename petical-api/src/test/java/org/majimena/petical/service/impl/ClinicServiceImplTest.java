package org.majimena.petical.service.impl;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petical.domain.Authority;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.ClinicStaff;
import org.majimena.petical.domain.User;
import org.majimena.petical.repository.ChartRepository;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.ClinicStaffRepository;
import org.majimena.petical.repository.InvoiceRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.repository.UserRepository;
import org.majimena.petical.security.SecurityUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ClinicServiceImpl
 */
@RunWith(Enclosed.class)
public class ClinicServiceImplTest {

    private static User createTestUser() {
        return User.builder()
                .id("1")
                .login("test")
                .build();
    }

    private static Clinic newClinic() {
        return Clinic.builder()
                .id("1")
                .name("Clinic Name")
                .lastName("LastName")
                .firstName("FirstName")
                .zipCode("1110000")
                .state("Tokyo")
                .city("Shinjuku")
                .street("Kabuki-cho 1-1-1")
                .phoneNo("0311110000")
                .build();
    }

    public static class GetMyClinicsByUserIdTest {

        @Tested
        private ClinicServiceImpl sut = new ClinicServiceImpl();
        @Injectable
        private ClinicRepository clinicRepository;
        @Injectable
        private ClinicStaffRepository clinicStaffRepository;
        @Injectable
        private UserRepository userRepository;
        @Injectable
        private TicketRepository ticketRepository;
        @Injectable
        private ChartRepository chartRepository;
        @Injectable
        private InvoiceRepository invoiceRepository;
        @Mocked
        private SecurityUtils securityUtils;

        @Test
        public void ユーザの勤務先のクリニック一覧が取得できること() throws Exception {
            new NonStrictExpectations() {{
                clinicStaffRepository.findClinicsByUserId("1");
                result = Arrays.asList(newClinic());
            }};

            List<Clinic> result = sut.getMyClinicsByUserId("1");

            assertThat(result.size(), is(1));
            assertThat(result.get(0).getId(), is("1"));
            assertThat(result.get(0).getName(), is("Clinic Name"));
            assertThat(result.get(0).getLastName(), is("LastName"));
            assertThat(result.get(0).getFirstName(), is("FirstName"));
            assertThat(result.get(0).getZipCode(), is("1110000"));
            assertThat(result.get(0).getState(), is("Tokyo"));
            assertThat(result.get(0).getCity(), is("Shinjuku"));
            assertThat(result.get(0).getStreet(), is("Kabuki-cho 1-1-1"));
            assertThat(result.get(0).getPhoneNo(), is("0311110000"));
        }
    }

    public static class GetClinicByIdTest {

        @Tested
        private ClinicServiceImpl sut = new ClinicServiceImpl();
        @Injectable
        private ClinicRepository clinicRepository;
        @Injectable
        private ClinicStaffRepository clinicStaffRepository;
        @Injectable
        private UserRepository userRepository;
        @Injectable
        private TicketRepository ticketRepository;
        @Injectable
        private ChartRepository chartRepository;
        @Injectable
        private InvoiceRepository invoiceRepository;

        @Test
        public void クリニックが取得できること() throws Exception {
            new NonStrictExpectations() {{
                clinicRepository.findOne("1");
                result = newClinic();
            }};

            Optional<Clinic> optional = sut.getClinicById("1");
            Clinic result = optional.get();

            assertThat(result.getId(), is("1"));
            assertThat(result.getName(), is("Clinic Name"));
            assertThat(result.getLastName(), is("LastName"));
            assertThat(result.getFirstName(), is("FirstName"));
            assertThat(result.getZipCode(), is("1110000"));
            assertThat(result.getState(), is("Tokyo"));
            assertThat(result.getCity(), is("Shinjuku"));
            assertThat(result.getStreet(), is("Kabuki-cho 1-1-1"));
            assertThat(result.getPhoneNo(), is("0311110000"));
        }
    }

    public static class SaveClinicTest {

        @Tested
        private ClinicServiceImpl sut = new ClinicServiceImpl();
        @Injectable
        private ClinicRepository clinicRepository;
        @Injectable
        private ClinicStaffRepository clinicStaffRepository;
        @Injectable
        private UserRepository userRepository;
        @Injectable
        private TicketRepository ticketRepository;
        @Injectable
        private ChartRepository chartRepository;
        @Injectable
        private InvoiceRepository invoiceRepository;
        @Mocked
        private SecurityUtils securityUtils;

        @Test
        public void 正しく登録されること() throws Exception {
            Clinic data = newClinic();

            new NonStrictExpectations() {{
                clinicRepository.save(data);
                data.setId("1");
                result = data;
                SecurityUtils.getCurrentUserId();
                result = "taro";
                userRepository.findOne("taro");
                result = User.builder().id("taro").build();
            }};

            Clinic result = sut.saveClinic(data);

            assertThat(result.getId(), is("1"));
            assertThat(result.getName(), is("Clinic Name"));
            assertThat(result.getLastName(), is("LastName"));
            assertThat(result.getFirstName(), is("FirstName"));
            assertThat(result.getZipCode(), is("1110000"));
            assertThat(result.getState(), is("Tokyo"));
            assertThat(result.getCity(), is("Shinjuku"));
            assertThat(result.getStreet(), is("Kabuki-cho 1-1-1"));
            assertThat(result.getPhoneNo(), is("0311110000"));

            new Verifications() {{
                ClinicStaff staff;
                clinicStaffRepository.save(staff = withCapture());

                assertThat(staff.getId(), is(nullValue()));
                assertThat(staff.getClinic(), is(notNullValue()));
                assertThat(staff.getClinic().getId(), is("1"));
                assertThat(staff.getUser(), is(notNullValue()));
                assertThat(staff.getUser().getId(), is("taro"));
                assertThat(staff.getRole(), is("ROLE_OWNER"));
                assertThat(staff.getActivatedDate(), is(notNullValue()));
            }};
        }
    }

    public static class GetClinicStaffsByIdTest {

        @Tested
        private ClinicServiceImpl sut = new ClinicServiceImpl();
        @Injectable
        private ClinicRepository clinicRepository;
        @Injectable
        private ClinicStaffRepository clinicStaffRepository;
        @Injectable
        private UserRepository userRepository;
        @Injectable
        private TicketRepository ticketRepository;
        @Injectable
        private ChartRepository chartRepository;
        @Injectable
        private InvoiceRepository invoiceRepository;
        @Mocked
        private SecurityUtils securityUtils;

        @Test
        public void クリニックに所属するスタッフが取得できること() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.getCurrentUserId();
                result = "u1";
                clinicStaffRepository.findByClinicIdAndUserId("c1", "u1");
                result = Optional.of(ClinicStaff.builder().id("1").build());
                clinicStaffRepository.findByClinicId("c1");
                result = Arrays.asList(ClinicStaff.builder().id("cs1")
                        .clinic(Clinic.builder().id("c1").build())
                        .user(User.builder().id("u1").authorities(new HashSet<>(Arrays.asList(new Authority("ROLE_USER")))).build())
                        .role("ROLE_TEST").build());
            }};

            List<ClinicStaff> result = sut.getClinicStaffsById("c1");

            assertThat(result.get(0).getId(), is("cs1"));
            assertThat(result.get(0).getClinic().getId(), is("c1"));
            assertThat(result.get(0).getUser().getId(), is("u1"));
            assertThat(result.get(0).getUser().getAuthorities().size(), is(1));
            assertThat(result.get(0).getRole(), is("ROLE_TEST"));
        }
    }
}
