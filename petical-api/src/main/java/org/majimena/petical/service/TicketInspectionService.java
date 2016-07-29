package org.majimena.petical.service;

import org.majimena.petical.domain.TicketInspection;

import java.util.List;
import java.util.Optional;

/**
 * チケット検査サービス.
 */
public interface TicketInspectionService {

    List<TicketInspection> getTicketInspectionsByTicketId(String ticketId);

    TicketInspection saveTicketInspection(TicketInspection inspection);

    void removeTicketInspectionById(String ticketInspectionId);

}
