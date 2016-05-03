package org.majimena.petical.service.impl;

import org.majimena.petical.common.aws.AmazonS3Service;
import org.majimena.petical.common.utils.BeanFactoryUtils;
import org.majimena.petical.common.utils.ExceptionUtils;
import org.majimena.petical.domain.Ticket;
import org.majimena.petical.domain.TicketAttachment;
import org.majimena.petical.domain.ticket.TicketAttachmentCriteria;
import org.majimena.petical.repository.TicketAttachmentRepository;
import org.majimena.petical.repository.TicketRepository;
import org.majimena.petical.repository.spec.TicketAttachmentSpecs;
import org.majimena.petical.service.TicketAttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * チケットの添付ファイルサービスの実装.
 */
@Service
@Transactional
public class TicketAttachmentServiceImpl implements TicketAttachmentService {

    /**
     * チケット添付ファイルリポジトリ.
     */
    @Inject
    private TicketAttachmentRepository ticketAttachmentRepository;

    /**
     * チケットリポジトリ.
     */
    @Inject
    private TicketRepository ticketRepository;

    /**
     * S3サービス.
     */
    @Inject
    private AmazonS3Service amazonS3Service;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketAttachment> getTicketAttachmentsByTicketAttachmentCriteria(TicketAttachmentCriteria criteria) {
        List<TicketAttachment> attachments = ticketAttachmentRepository.findAll(TicketAttachmentSpecs.of(criteria), TicketAttachmentSpecs.asc());
        return attachments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TicketAttachment> getTicketAttachmentByTicketAttachmentId(String attachmentId) {
        TicketAttachment one = ticketAttachmentRepository.findOne(attachmentId);
        one.getTicket();
        return Optional.ofNullable(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TicketAttachment saveTicketAttachment(String clinicId, String ticketId, String name, byte[] binary) {
        // チケットが存在するか、指定クリニックのチケットであるかをチェックする
        Ticket ticket = ticketRepository.findOne(ticketId);
        ExceptionUtils.throwIfNull(ticket);
        ExceptionUtils.throwIfNotEqual(clinicId, ticket.getClinic().getId());

        // 先にS3にファイルを保存
        String filename = "clinics/" + clinicId + "/tickets/" + ticketId + "/" + name;
        String url = amazonS3Service.upload(filename, binary);

        // エンティティにファイル保存先を指定
        TicketAttachment attachment = new TicketAttachment();
        attachment.setTicket(ticket);
        attachment.setName(name);
        attachment.setUrl(url);
        attachment.setRemoved(Boolean.FALSE);

        // DBに保存する
        return ticketAttachmentRepository.save(attachment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TicketAttachment updateTicketAttachment(TicketAttachment attachment) {
        // 更新する情報をエンティティにコピー
        TicketAttachment one = ticketAttachmentRepository.findOne(attachment.getId());
        ExceptionUtils.throwIfNull(one);
        BeanFactoryUtils.copyNonNullProperties(attachment, one);

        // DBを更新する
        return ticketAttachmentRepository.save(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteTicketAttachmentByTicketAttachmentId(String clinicId, String ticketId, String attachmentId) {
        // 削除対象が存在するかチェックする
        TicketAttachment one = ticketAttachmentRepository.findOne(attachmentId);
        ExceptionUtils.throwIfNull(one);

        // 削除可能なクリニックの情報かチェックする
        String id = one.getTicket().getClinic().getId();
        ExceptionUtils.throwIfNotEqual(id, clinicId);

        // チケットと添付ファイルを削除する
        String url = one.getUrl();
        ticketAttachmentRepository.delete(one);
        amazonS3Service.delete(url);
    }
}
