package org.majimena.petz.service.impl;

import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.majimena.petz.datatype.InvoiceState;
import org.majimena.petz.datatype.PaymentType;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.domain.Invoice;
import org.majimena.petz.domain.Payment;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.repository.InvoiceRepository;
import org.majimena.petz.repository.PaymentRepository;
import org.majimena.petz.repository.TicketRepository;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @see PaymentServiceImpl
 */
@RunWith(Enclosed.class)
public class PaymentServiceImplTest {

    private static Invoice newInvoice() {
        return Invoice.builder()
                .total(BigDecimal.valueOf(10800))
                .tax(BigDecimal.valueOf(800))
                .subtotal(BigDecimal.valueOf(10000))
                .receiptAmount(BigDecimal.ZERO)
                .ticket(new Ticket())
                .paid(Boolean.FALSE)
                .removed(Boolean.FALSE)
                .build();
    }

    public static class SavePaymentTest {

        @Tested
        private PaymentServiceImpl sut = new PaymentServiceImpl();
        @Injectable
        private PaymentRepository paymentRepository;
        @Injectable
        private InvoiceRepository invoiceRepository;
        @Injectable
        private TicketRepository ticketRepository;

        @Test
        public void 全額精算した場合に正しく請求情報がクリアされること() throws Exception {
            Payment payment = Payment.builder().type(PaymentType.CASH).amount(BigDecimal.valueOf(10800)).build();
            payment.setInvoice(newInvoice());

            sut.savePayment(payment);

            new Verifications() {{
                Invoice invoice;
                invoiceRepository.save(invoice = withCapture());
                assertThat(invoice.getReceiptAmount(), is(BigDecimal.valueOf(10800)));
                assertThat(invoice.getReceiptDateTime(), is(notNullValue()));
                assertThat(invoice.getPaid(), is(Boolean.TRUE));

                Ticket ticket;
                ticketRepository.save(ticket = withCapture());
                assertThat(ticket.getState(), is(TicketState.COMPLETED));

                Payment result;
                paymentRepository.save(result = withCapture());
                assertThat(result.getAmount(), is(BigDecimal.valueOf(10800)));
                assertThat(result.getType(), is(PaymentType.CASH));
                assertThat(result.getMemo(), is(nullValue()));
            }};
        }

        @Test
        public void 未払い金が発生する場合は請求情報がクリアされないこと() throws Exception {
            Payment payment = Payment.builder().type(PaymentType.CASH).amount(BigDecimal.valueOf(5000)).build();
            payment.setInvoice(newInvoice());

            sut.savePayment(payment);

            new Verifications() {{
                Invoice invoice;
                invoiceRepository.save(invoice = withCapture());
                assertThat(invoice.getReceiptAmount(), is(BigDecimal.valueOf(5000)));
                assertThat(invoice.getReceiptDateTime(), is(nullValue()));
                assertThat(invoice.getPaid(), is(Boolean.FALSE));

                Payment result;
                paymentRepository.save(result = withCapture());
                assertThat(result.getAmount(), is(BigDecimal.valueOf(5000)));
                assertThat(result.getType(), is(PaymentType.CASH));
                assertThat(result.getMemo(), is(nullValue()));
            }};
        }
    }


}
