package org.majimena.petz.web.api.ticket;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.TicketAttachment;
import org.majimena.petz.domain.ticket.TicketAttachmentCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.TicketAttachmentService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * チケット添付ファイルコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicTicketAttachmentController {

    /**
     * チケット添付ファイルサービス.
     */
    @Inject
    private TicketAttachmentService ticketAttachmentService;

    /**
     * チケット添付ファイルのバリデータ.
     */
    @Inject
    private TicketAttachmentValidator ticketAttachmentValidator;

    /**
     * ログインユーザのチケット添付ファイルチケットを取得する.
     *
     * @param criteria チケットクライテリア
     * @return 該当するチケット一覧
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/attachments", method = RequestMethod.GET)
    public ResponseEntity<List<TicketAttachment>> get(@PathVariable String clinicId, @PathVariable String ticketId,
                                                      @Valid TicketAttachmentCriteria criteria) {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 検索する
        criteria.setClinicId(clinicId);
        criteria.setTicketId(ticketId);
        List<TicketAttachment> attachments = ticketAttachmentService.getTicketAttachmentsByTicketAttachmentCriteria(criteria);
        return ResponseEntity.ok().body(attachments);
    }

    /**
     * チケット添付ファイルを新規作成する.
     *
     * @param clinicId クリニックID
     * @param ticketId チケットID
     * @param file     アップロードファイル
     * @return 登録したチケット添付ファイル情報
     * @throws BindException 入力内容に不備がある場合
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/attachments", method = RequestMethod.POST)
    public ResponseEntity<TicketAttachment> post(@PathVariable String clinicId, @PathVariable String ticketId,
                                                 @RequestParam("file") MultipartFile file) throws IOException {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // チケット添付ファイル情報を保存する
        TicketAttachment saved = ticketAttachmentService.saveTicketAttachment(clinicId, ticketId, file.getOriginalFilename(), file.getBytes());
        return ResponseEntity.created(
                URI.create("/api/v1/clinics/" + clinicId + "/tickets/" + ticketId + "/attachments/" + saved.getId())).body(saved);
    }

    /**
     * チケット添付ファイルを更新する.
     *
     * @param clinicId     クリニックID
     * @param ticketId     チケットID
     * @param attachmentId チケット添付ファイルID
     * @param attachment   更新するチケット添付ファイル情報
     * @param errors       エラーオブジェクト
     * @return 更新したチケット添付ファイル情報
     * @throws BindException 入力内容に不備がある場合
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/attachments/{attachmentId}", method = RequestMethod.PUT)
    public ResponseEntity<TicketAttachment> put(@PathVariable String clinicId, @PathVariable String ticketId, @PathVariable String attachmentId,
                                                @Valid @RequestBody TicketAttachment attachment, BindingResult errors) throws BindException, IOException {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        ErrorsUtils.throwIfNotIdentify(attachmentId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        ticketAttachmentValidator.validate(attachment, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // チケット添付ファイル情報を更新する
        TicketAttachment saved = ticketAttachmentService.updateTicketAttachment(attachment);
        return ResponseEntity.ok().body(saved);
    }

    /**
     * チケット添付ファイルを削除する.
     *
     * @param clinicId     クリニックID
     * @param ticketId     チケットID
     * @param attachmentId チケット添付ファイルID
     * @return レスポンスステータス（200）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/attachments/{attachmentId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String ticketId, @PathVariable String attachmentId) {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        ErrorsUtils.throwIfNotIdentify(attachmentId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // チケット添付ファイルを削除する
        ticketAttachmentService.deleteTicketAttachmentByTicketAttachmentId(clinicId, ticketId, attachmentId);
        return ResponseEntity.ok().build();
    }
}
