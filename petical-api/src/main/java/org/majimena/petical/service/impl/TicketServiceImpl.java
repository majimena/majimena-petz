package org.majimena.petical.service.impl;

import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.datatype.TicketActivityType;
import org.majimena.petical.datatype.TicketState;
import org.majimena.petical.datetime.L10nDateTimeProvider;
import org.majimena.petical.domain.Chart;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.domain.TicketActivity;
import org.majimena.petical.domain.TicketAttachment;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.domain.ticket.ClinicChartTicketCriteria;
import org.majimena.petical.domain.ticket.TicketCriteria;
import org.majimena.petical.repository.ChartRepository;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.CustomerRepository;
import org.majimena.petical.repository.PetRepository;
import org.majimena.petical.repository.TicketActivityRepository;
import org.majimena.petical.repository.TicketAttachmentRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.repository.spec.TicketSpecs;
import org.majimena.petical.service.TicketService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * チケットサービスの実装.
 */
@Service
public class TicketServiceImpl implements TicketService {

    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * チケットアクティビティリポジトリ.
     */
    @Inject
    private TicketActivityRepository ticketActivityRepository;

    /**
     * クリニックリポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * ペットリポジトリ.
     */
    @Inject
    private PetRepository petRepository;

    /**
     * 顧客リポジトリ.
     */
    @Inject
    private CustomerRepository customerRepository;

    /**
     * カルテリポジトリ.
     */
    @Inject
    private ChartRepository chartRepository;

    @Inject
    private TicketAttachmentRepository ticketAttachmentRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByTicketCriteria(TicketCriteria criteria) {
        List<Ticket> tickets = ticketRepository.findAll(TicketSpecs.of(criteria));
        tickets.forEach(ticket -> {
            // lazy loading TODO eager fetchしないと性能が良くないかも
            ticket.getChart().getCustomer().getId();
            ticket.getChart().getCustomer().getUser().getId();
            ticket.getChart().getPet().getId();
        });
        return tickets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ticket> getTicketsByClinicChartTicketCriteria(ClinicChartTicketCriteria criteria) {
        // TODO 個別でサービス作ったほうが保守はしやすいかも
        List<Ticket> tickets = ticketRepository.findAll(TicketSpecs.of(criteria), TicketSpecs.desc());
        tickets.forEach(ticket -> {
            if (ticket.getDiagnosis() != null) {
                ticket.getDiagnosis().getId();
            }
        });
        return tickets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> getTicketByTicketId(String ticketId) {
        return Optional.ofNullable(ticketRepository.getOne(ticketId))
                .map(ticket -> {
                    // lazy load other entities
                    ticket.getChart().getId();
                    ticket.getChart().getPet().getId();
                    ticket.getChart().getCustomer().getId();
                    ticket.getChart().getCustomer().getUser().getId();
                    if (ticket.getDiagnosis() != null) {
                        ticket.getDiagnosis().getId();
                    }
                    return ticket;
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ticket saveTicket(Ticket ticket) {
        // クリニックを特定する
        Clinic clinic = clinicRepository.findOne(ticket.getClinic().getId());
        ExceptionUtils.throwIfNull(clinic);

        // カルテを特定する
        Chart chart = chartRepository.findOne(ticket.getChart().getId());
        ExceptionUtils.throwIfNull(chart);

        // チケットと関連マスタを関連付けて保存する
        ticket.setClinic(clinic);
        ticket.setChart(chart);
        ticket.setState(TicketState.RESERVED);
        Ticket saved = ticketRepository.save(ticket);

        // アクティビティを保存
        saveTicketActivity(saved, TicketActivityType.CHANGE_STATE, TicketState.NULL, TicketState.RESERVED);
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ticket updateTicket(Ticket ticket) {
        // クリニックを特定する
        Clinic clinic = clinicRepository.findOne(ticket.getClinic().getId());
        ExceptionUtils.throwIfNull(clinic);

        // カルテを特定する
        Chart chart = chartRepository.findOne(ticket.getChart().getId());
        ExceptionUtils.throwIfNull(chart);

        // 永続化されているチケットを取得して、そちらに値をコピーする
        Ticket entity = ticketRepository.findOne(ticket.getId());
        BeanUtils.copyProperties(ticket, entity);
        entity.setClinic(clinic);
        entity.setChart(chart);
        return ticketRepository.save(ticket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteTicketByTicketId(String ticketId) {
        // 削除対象のチケットがあるかチェック
        Ticket one = ticketRepository.findOne(ticketId);
        ExceptionUtils.throwIfNull(one);

        // 関連する添付ファイルを論理削除する
        List<TicketAttachment> attachments = ticketAttachmentRepository.findByTicketId(ticketId);
        attachments.stream().forEach(attachment -> {
            attachment.setRemoved(Boolean.TRUE);
            ticketAttachmentRepository.save(attachment);
        });

        // アクティビティを記録
        saveTicketActivity(one, TicketActivityType.CHANGE_STATE, one.getState(), TicketState.CANCEL);

        // チケットを論理削除する
        one.setState(TicketState.CANCEL);
        ticketRepository.save(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ticket signalTicketStatus(String ticketId) {
        return Optional.ofNullable(ticketRepository.findOne(ticketId))
                .map(ticket -> {
                    // アクティビティを記録
                    TicketState next = ticket.getState().next();
                    saveTicketActivity(ticket, TicketActivityType.CHANGE_STATE, ticket.getState(), next);

                    // ステータスを変更
                    ticket.setState(next);
                    return ticketRepository.save(ticket);
                }).orElseThrow(() -> new ApplicationException(ErrorCode.PTZ_100999));
    }

    protected void saveTicketActivity(Ticket ticket, TicketActivityType type, TicketState from, TicketState to) {
        LocalDateTime date = L10nDateTimeProvider.now().toLocalDateTime();
        TicketActivity activity = TicketActivity.builder()
                .ticket(ticket).type(type).stateFrom(from).stateTo(to).changeDateTime(date).build();
        ticketActivityRepository.save(activity);
    }
}
