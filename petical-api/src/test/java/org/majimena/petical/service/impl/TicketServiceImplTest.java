package org.majimena.petical.service.impl;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.domain.Chart;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.Customer;
import org.majimena.petical.domain.Pet;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.domain.User;
import org.majimena.petical.domain.ticket.TicketCriteria;
import org.majimena.petical.repository.ChartRepository;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.CustomerRepository;
import org.majimena.petical.repository.PetRepository;
import org.majimena.petical.repository.TicketActivityRepository;
import org.majimena.petical.repository.TicketAttachmentRepository;
import org.majimena.petical.repository.TicketRepository;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see TicketServiceImpl
 */
@RunWith(Enclosed.class)
public class TicketServiceImplTest {

    private static Ticket newTicket() {
        return Ticket.builder()
                .id("ticket1")
                .clinic(Clinic.builder().id("clinic1").build())
                .chart(Chart.builder().id("chart1")
                        .customer(Customer.builder().id("customer1").user(User.builder().id("user1").build()).build())
                        .pet(Pet.builder().id("pet1").build())
                        .build())
                .state(TicketState.RESERVED)
                .startDateTime(LocalDateTime.of(2015, 11, 1, 0, 0, 0))
                .endDateTime(LocalDateTime.of(2015, 11, 1, 1, 0, 0))
                .memo("memo")
                .build();
    }

    public static class GetTicketsByTicketCriteriaTest {

        @Tested
        private TicketServiceImpl sut = new TicketServiceImpl();
        @Injectable
        private TicketRepository ticketRepository;
        @Injectable
        private TicketActivityRepository ticketActivityRepository;
        @Injectable
        private ClinicRepository clinicRepository;
        @Injectable
        private PetRepository petRepository;
        @Injectable
        private CustomerRepository customerRepository;
        @Injectable
        private ChartRepository chartRepository;
        @Injectable
        private TicketAttachmentRepository ticketAttachmentRepository;

        @Test
        public void チケットの一覧が取得できること() {
            TicketCriteria criteria = TicketCriteria.builder().clinicId("clinic1").year(2015).month(11).build();

            new NonStrictExpectations() {{
                ticketRepository.findAll((Specification) any);
                result = Arrays.asList(newTicket());
            }};

            List<Ticket> result = sut.getTicketsByTicketCriteria(criteria);

            assertThat(result.size(), is(1));
            assertThat(result.get(0).getId(), is("ticket1"));
            assertThat(result.get(0).getMemo(), is("memo"));
            assertThat(result.get(0).getStartDateTime(), is(LocalDateTime.of(2015, 11, 1, 0, 0, 0)));
            assertThat(result.get(0).getEndDateTime(), is(LocalDateTime.of(2015, 11, 1, 1, 0, 0)));
            assertThat(result.get(0).getClinic().getId(), is("clinic1"));
            assertThat(result.get(0).getChart().getId(), is("chart1"));
            assertThat(result.get(0).getChart().getCustomer().getId(), is("customer1"));
            assertThat(result.get(0).getChart().getCustomer().getUser().getId(), is("user1"));
            assertThat(result.get(0).getChart().getPet().getId(), is("pet1"));
        }
    }
}
