package org.majimena.petz.service.impl;

import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.datatype.InvoiceState;
import org.majimena.petz.datatype.TaxType;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Examination;
import org.majimena.petz.domain.Invoice;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.repository.ExaminationRepository;
import org.majimena.petz.repository.InvoiceRepository;
import org.majimena.petz.repository.TicketRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @see InvoiceServiceImpl
 */
@RunWith(Enclosed.class)
public class InvoiceServiceImplTest {

    private static List<Examination> newExaminations() {
        Examination exam1 = Examination.builder()
                .id("exam1")
                .price(BigDecimal.valueOf(1000L))
                .quantity(BigDecimal.valueOf(2))
                .total(BigDecimal.valueOf(2160L))
                .taxType(TaxType.EXCLUSIVE)
                .taxRate(BigDecimal.valueOf(0.08))
                .tax(BigDecimal.valueOf(160L))
                .build();
        Examination exam2 = Examination.builder()
                .id("exam2")
                .price(BigDecimal.valueOf(10000L))
                .quantity(BigDecimal.valueOf(1))
                .total(BigDecimal.valueOf(10000L))
                .taxType(TaxType.INCLUSIVE)
                .taxRate(BigDecimal.valueOf(0.08))
                .tax(BigDecimal.valueOf(741L))
                .build();
        return Arrays.asList(exam1, exam2);
    }

    public static class CreateInvoiceTest {

        @Tested
        private InvoiceServiceImpl sut = new InvoiceServiceImpl();
        @Injectable
        private InvoiceRepository invoiceRepository;
        @Injectable
        private TicketRepository ticketRepository;
        @Injectable
        private ExaminationRepository examinationRepository;

        @Test
        public void 正しく計算された請求情報が作成されること() throws Exception {
            new NonStrictExpectations() {{
                ticketRepository.findOne("ticket1");
                result = Ticket.builder().id("ticket1").clinic(Clinic.builder().id("1").build()).build();
                examinationRepository.findByTicketId("ticket1");
                result = newExaminations();
            }};

            sut.createInvoice("1", "ticket1");

            new Verifications() {{
                Ticket ticket;
                ticketRepository.save(ticket = withCapture());
                Invoice invoice;
                invoiceRepository.save(invoice = withCapture());

                assertThat(ticket.getId(), is("ticket1"));
                assertThat(ticket.getState(), is(TicketState.PAYMENT));

                assertThat(invoice.getId(), is(nullValue()));
                assertThat(invoice.getState(), is(InvoiceState.NOT_PAID));
                assertThat(invoice.getTotal(), is(BigDecimal.valueOf(12160L)));
                assertThat(invoice.getSubtotal(), is(BigDecimal.valueOf(11259L)));
                assertThat(invoice.getTax(), is(BigDecimal.valueOf(901L)));
                assertThat(invoice.getReceiptAmount(), is(BigDecimal.ZERO));
                assertThat(invoice.getReceiptDateTime(), is(nullValue()));
            }};
        }
    }


}
