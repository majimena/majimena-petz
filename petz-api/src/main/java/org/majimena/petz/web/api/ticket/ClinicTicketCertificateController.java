package org.majimena.petz.web.api.ticket;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Certificate;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Ticket;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.CertificateService;
import org.majimena.petz.service.TicketService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * クリニックチケットの証明書発行コントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicTicketCertificateController {

    /**
     * 証明書サービス.
     */
    @Inject
    private CertificateService certificateService;

    /**
     * チケットサービス.
     */
    @Inject
    private TicketService ticketService;

    /**
     * 証明書バリデータ.
     */
    @Inject
    private CertificateValidator certificateValidator;

    /**
     * クリニックの証明書を取得する.
     *
     * @param clinicId クリニックID
     * @param ticketId 証明書ID
     * @return レスポンスエンティティ（通常時は200、権限エラー時は401）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/certificates", method = RequestMethod.GET)
    public ResponseEntity<List<Certificate>> get(@PathVariable String clinicId, @PathVariable String ticketId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // チケットの参照権限があるかチェックする
        ticketService.getTicketByTicketId(ticketId)
                .ifPresent(ticket -> ErrorsUtils.throwIfNotEqual(clinicId, ticket.getClinic().getId()));

        // 該当する証明書を取得する
        List<Certificate> list = certificateService.getCertificatesByTicketId(ticketId);
        return ResponseEntity.ok().body(list);
    }

    /**
     * クリニックの証明書を取得する.
     *
     * @param clinicId クリニックID
     * @param ticketId 証明書ID
     * @return レスポンスエンティティ（通常時は200、権限エラー時は401、結果がない場合は404）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/certificates/{certificateId}", method = RequestMethod.GET)
    public ResponseEntity<Certificate> get(@PathVariable String clinicId, @PathVariable String ticketId, @PathVariable String certificateId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        ErrorsUtils.throwIfNotIdentify(certificateId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 該当する証明書を取得する
        return certificateService.getCertificateByCertificateId(certificateId)
                .map(certificate -> {
                    Ticket ticket = certificate.getTicket();
                    ErrorsUtils.throwIfNotEqual(ticketId, ticket.getId());
                    ErrorsUtils.throwIfNotEqual(clinicId, ticket.getClinic().getId());
                    return ResponseEntity.ok().body(certificate);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * クリニックの証明書を新規作成する.
     *
     * @param clinicId    クリニックID
     * @param ticketId    証明書ID
     * @param certificate 登録する証明書情報
     * @param errors      エラーオブジェクト
     * @return レスポンスステータス（正常時は200、権限エラー時は401、異常時は500）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは400）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/certificates", method = RequestMethod.POST)
    public ResponseEntity<Certificate> post(@PathVariable String clinicId, @PathVariable String ticketId,
                                            @RequestBody @Valid Certificate certificate, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        Clinic clinic = Clinic.builder().id(clinicId).build();
        Ticket ticket = Ticket.builder().id(ticketId).clinic(clinic).build();
        certificate.setTicket(ticket);
        certificateValidator.validate(certificate, errors);
        ErrorsUtils.throwIfHaveErrors(errors);

        // 証明書を保存する
        Certificate saved = certificateService.saveCertificate(certificate);
        return ResponseEntity.created(
                URI.create("/api/v1/clinics/" + clinicId + "/tickets/" + ticketId + "/certificates")).body(saved);
    }

    /**
     * 証明書内で発行した証明書を削除する.
     *
     * @param clinicId      クリニックID
     * @param ticketId      証明書ID
     * @param certificateId 証明書ID
     * @return レスポンスステータス（正常時は200、権限エラー時は401、異常時は500）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/certificates/{certificateId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String ticketId, @PathVariable String certificateId) {
        // クリニックの権限チェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        ErrorsUtils.throwIfNotIdentify(certificateId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // エラーにならなければ消しても良いということ
        ResponseEntity<Certificate> entity = get(clinicId, ticketId, certificateId);
        if (entity.getStatusCode().is2xxSuccessful()) {
            Certificate certificate = entity.getBody();
            certificateService.deleteCertificate(certificate);
        }

        // 基本的にいつも成功したことにしておく
        return ResponseEntity.ok().build();
    }
}
