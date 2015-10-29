package org.majimena.petz.service.impl;

import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.datatype.TicketActivityType;
import org.majimena.petz.datatype.TicketState;
import org.majimena.petz.datetime.L10nDateTimeProvider;
import org.majimena.petz.domain.Chart;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.domain.TicketActivity;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.domain.examination.TicketCriteria;
import org.majimena.petz.repository.ChartRepository;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.CustomerRepository;
import org.majimena.petz.repository.PetRepository;
import org.majimena.petz.repository.TicketActivityRepository;
import org.majimena.petz.repository.TicketRepository;
import org.majimena.petz.repository.spec.ScheduleCriteriaSpec;
import org.majimena.petz.service.TicketService;
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByTicketCriteria(TicketCriteria criteria) {
        return ticketRepository.findAll(new ScheduleCriteriaSpec(criteria));
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
        Optional.ofNullable(ticketRepository.findOne(ticketId)).ifPresent(ticket -> {
            // アクティビティを記録
            saveTicketActivity(ticket, TicketActivityType.CHANGE_STATE, ticket.getState(), TicketState.CANCEL);

            // チケットを削除
            ticket.setState(TicketState.CANCEL);
            ticket.setRemoved(Boolean.TRUE);
            ticketRepository.save(ticket);
        });
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
