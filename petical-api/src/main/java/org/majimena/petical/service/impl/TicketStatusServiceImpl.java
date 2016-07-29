package org.majimena.petical.service.impl;

import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.service.TicketStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * チケットステータスサービスの実装.
 */
@Service
@Transactional
public class TicketStatusServiceImpl implements TicketStatusService {
    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Ticket updateTicketStatus(String id, TicketState state) {
        Ticket ticket = ticketRepository.findOne(id);
        ExceptionUtils.throwIfNull(ticket);

        // FIXME 変えられないステートだけ定義して、それをチェックしても良いかもしれない
        ticket.setState(state);
        return ticketRepository.save(ticket);
    }
}
