package org.majimena.petz.service.impl;

import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.common.utils.ExceptionUtils;
import org.majimena.petz.datatype.TicketActivityType;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.datetime.L10nDateTimeProvider;
import org.majimena.petz.domain.Chart;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.Examination;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.TicketActivity;
import org.majimena.petz.domain.TicketAttachment;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.domain.graph.Graph;
import org.majimena.petz.domain.ticket.TicketCriteria;
import org.majimena.petz.repository.ChartRepository;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.CustomerRepository;
import org.majimena.petz.repository.ExaminationRepository;
import org.majimena.petz.repository.PetRepository;
import org.majimena.petz.repository.TicketActivityRepository;
import org.majimena.petz.repository.TicketAttachmentRepository;
import org.majimena.petz.repository.TicketRepository;
import org.majimena.petz.repository.spec.TicketSpecs;
import org.majimena.petz.service.TicketService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * 診察リポジトリ.
     */
    @Inject
    private ExaminationRepository examinationRepository;
    @Inject
    private TicketAttachmentRepository ticketAttachmentRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByTicketCriteria(TicketCriteria criteria) {
        List<Ticket> tickets = ticketRepository.findAll(TicketSpecs.of(criteria));
        return tickets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> getTicketByTicketId(String ticketId) {
        Ticket ticket = ticketRepository.getOne(ticketId);
        return Optional.ofNullable(ticket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Graph getTodaysTicketGraphByClinicId(String clinicId) {
        ZonedDateTime now = L10nDateTimeProvider.now();

        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime from = L10nDateTimeProvider.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), i);
            LocalDateTime to = L10nDateTimeProvider.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), i, 59, 59);
            long count = ticketRepository.count(TicketSpecs.of(clinicId, from, to));
            labels.add(i + ":00");
            values.add(BigDecimal.valueOf(count));
        }

        return new Graph(labels, Arrays.asList(values));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ticket saveTicket(Ticket ticket) {
        // 必須の関連マスタを取得する（事前にチェックしているので、通常データがないことはありえない）
        Clinic clinic = clinicRepository.findOne(ticket.getClinic().getId());
        Pet pet = petRepository.findOne(ticket.getPet().getId());
        User user = pet.getUser();

        // 顧客が特定できている場合はそのまま使い、顧客が特定できていない場合は既に顧客登録されていないか確認する
        Customer customer = Optional.ofNullable(ticket.getCustomer())
                .map(p -> customerRepository.findOne(p.getId()))
                .orElse(customerRepository.findByClinicIdAndUserId(clinic.getId(), user.getId()).orElse(null));
        // カルテが特定できる場合はカルテも紐付けする
        Chart chart = Optional.ofNullable(customer)
                .map(p -> chartRepository.findByClinicIdAndCustomerIdAndPetId(clinic.getId(), p.getId(), pet.getId()).orElse(null))
                .orElse(null);

        // チケットと関連マスタを関連付けて保存する
        ticket.setClinic(clinic);
        ticket.setPet(pet);
        ticket.setCustomer(customer);
        ticket.setChart(chart);
        ticket.setState(TicketState.RESERVED);
        ticket.setRemoved(Boolean.FALSE);
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
        // 永続化されているチケットを取得して、そちらに値をコピーする
        Ticket entity = ticketRepository.findOne(ticket.getId());
        BeanUtils.copyProperties(ticket, entity);

        // 必須の関連マスタを取得する（事前にチェックしているので、通常データがないことはありえない）
        Clinic clinic = clinicRepository.findOne(ticket.getClinic().getId());
        Pet pet = petRepository.findOne(ticket.getPet().getId());
        User user = pet.getUser();

        // 顧客が特定できている場合はそのまま使い、顧客が特定できていない場合は既に顧客登録されていないか確認する
        Customer customer = Optional.ofNullable(ticket.getCustomer())
                .map(p -> customerRepository.findOne(p.getId()))
                .orElse(customerRepository.findByClinicIdAndUserId(clinic.getId(), user.getId()).orElse(null));
        // カルテが特定できる場合はカルテも紐付けする
        Chart chart = Optional.ofNullable(customer)
                .map(p -> chartRepository.findByClinicIdAndCustomerIdAndPetId(clinic.getId(), p.getId(), pet.getId()).orElse(null))
                .orElse(null);

        // チケットと関連マスタを関連付けて保存する
        entity.setClinic(clinic);
        entity.setPet(pet);
        entity.setChart(chart);
        entity.setCustomer(customer);
        entity.setRemoved(Boolean.FALSE);
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

        // 先に関連する診察情報を全て論理削除する
        List<Examination> examinations = examinationRepository.findByTicketId(ticketId);
        examinations.stream().forEach(examination -> {
            examination.setRemoved(Boolean.TRUE);
            examinationRepository.save(examination);
        });

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
        one.setRemoved(Boolean.TRUE);
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
