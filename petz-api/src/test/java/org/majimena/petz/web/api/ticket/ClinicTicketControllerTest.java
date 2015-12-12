package org.majimena.petz.web.api.ticket;

import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.WebAppTestConfiguration;
import org.majimena.petz.config.SpringMvcConfiguration;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.ticket.TicketCriteria;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.TicketService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @see ClinicTicketController
 */
@RunWith(Enclosed.class)
public class ClinicTicketControllerTest {

    private static Ticket newTicket() {
        return Ticket.builder()
                .id("ticket1")
                .state(TicketState.RESERVED)
                .memo("1234567890")
                .build();
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = WebAppTestConfiguration.class)
    @WebAppConfiguration
    public static class GetAllTest {

        private MockMvc mockMvc;

        @Tested
        private ClinicTicketController sut = new ClinicTicketController();

        @Injectable
        private TicketService ticketService;

        @Injectable
        private TicketValidator ticketValidator;

        @Mocked
        private SecurityUtils securityUtils;

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.standaloneSetup(sut)
                    .setHandlerExceptionResolvers(new SpringMvcConfiguration().restExceptionResolver())
                    .build();
        }

        @Test
        public void 権限がない場合はアクセスできないこと() throws Exception {
            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = new ResourceCannotAccessException("");
            }};

            mockMvc.perform(get("/api/v1/clinics/1/tickets"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.type", is("https://httpstatuses.com/401")))
                    .andExpect(jsonPath("$.title", is("Unauthorized")))
                    .andExpect(jsonPath("$.status", is(401)))
                    .andExpect(jsonPath("$.detail", is("You cannot access resource.")));
        }

        @Test
        public void チケットの一覧が取得できること() throws Exception {
            TicketCriteria criteria = TicketCriteria.builder().build();
            criteria.setClinicId("1");

            new NonStrictExpectations() {{
                SecurityUtils.throwIfDoNotHaveClinicRoles("1");
                result = null;
                ticketService.getTicketsByTicketCriteria(criteria);
                result = Arrays.asList(newTicket());
            }};

            mockMvc.perform(get("/api/v1/clinics/1/tickets"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].id", is("ticket1")))
                    .andExpect(jsonPath("$.[0].clinic", is(nullValue())))
                    .andExpect(jsonPath("$.[0].pet", is(nullValue())))
                    .andExpect(jsonPath("$.[0].customer", is(nullValue())))
                    .andExpect(jsonPath("$.[0].chart", is(nullValue())))
                    .andExpect(jsonPath("$.[0].state", is(TicketState.RESERVED.name())))
                    .andExpect(jsonPath("$.[0].memo", is("1234567890")))
                    .andExpect(jsonPath("$.[0].startDateTime", is(nullValue())))
                    .andExpect(jsonPath("$.[0].endDateTime", is(nullValue())));
        }
    }
}
