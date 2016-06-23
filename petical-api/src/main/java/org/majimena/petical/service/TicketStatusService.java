package org.majimena.petical.service;

import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.domain.Ticket;

/**
 * チケットステータスサービス.
 */
public interface TicketStatusService {

    Ticket updateTicketStatus(String id, TicketState state);

}
