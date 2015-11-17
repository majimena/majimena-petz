package org.majimena.petz.web.api.ticket;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Examination;
import org.majimena.petz.domain.ticket.ExaminationCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ExaminationService;
import org.majimena.petz.web.utils.ErrorsUtils;
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
 * 診察コントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ExaminationController {

    /**
     * 診察サービス.
     */
    @Inject
    private ExaminationService examinationService;

    /**
     * 診察バリデータ.
     */
    @Inject
    private ExaminationValidator examinationValidator;

    /**
     * ログインユーザの診察チケットを取得する.
     *
     * @param criteria チケットクライテリア
     * @return 該当するチケット一覧
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/examinations", method = RequestMethod.GET)
    public ResponseEntity<List<Examination>> get(@PathVariable String clinicId, @PathVariable String ticketId,
                                                 @Valid ExaminationCriteria criteria) {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 検索する
        criteria.setClinicId(clinicId);
        criteria.setTicketId(ticketId);
        List<Examination> examinations = examinationService.getExaminationsByExaminationCriteria(criteria);
        return ResponseEntity.ok().body(examinations);
    }

    /**
     * チケットを新規作成する.
     *
     * @param clinicId    クリニックID
     * @param ticketId    チケットID
     * @param examination 登録する診察情報
     * @param errors      エラーオブジェクト
     * @return 登録した診察情報
     * @throws BindException 入力内容に不備がある場合
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/examinations", method = RequestMethod.POST)
    public ResponseEntity<Examination> post(@PathVariable String clinicId, @PathVariable String ticketId,
                                            @RequestBody @Valid Examination examination, BindingResult errors) throws BindException {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う（実際のクリニック権限チェックはバリデータ内で行う）
        examinationValidator.validate(examination, errors);
        ErrorsUtils.throwIfHaveErrors(errors);

        // チケットを保存する
        Examination saved = examinationService.saveExamination(examination);
        return ResponseEntity.created(
                URI.create("/api/v1/clinics/" + clinicId + "/tickets/" + ticketId + "/examinations/" + saved.getId())).body(saved);
    }

    /**
     * チケットを更新する.
     *
     * @param clinicId      クリニックID
     * @param ticketId      チケットID
     * @param examinationId 診察ID
     * @param examination   更新する診察情報
     * @param errors        エラーオブジェクト
     * @return 更新した診察情報
     * @throws BindException 入力内容に不備がある場合
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/examinations/{examinationId}", method = RequestMethod.PUT)
    public ResponseEntity<Examination> put(@PathVariable String clinicId, @PathVariable String ticketId, @PathVariable String examinationId,
                                           @RequestBody @Valid Examination examination, BindingResult errors) throws BindException {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        ErrorsUtils.throwIfNotIdentify(examinationId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う（実際のクリニック権限チェックはバリデータ内で行う）
        examinationValidator.validate(examination, errors);
        ErrorsUtils.throwIfHaveErrors(errors);

        // チケットを更新する
        Examination saved = examinationService.saveExamination(examination);
        return ResponseEntity.ok().body(saved);
    }

    /**
     * チケットを削除する.
     *
     * @param clinicId      クリニックID
     * @param ticketId      チケットID
     * @param examinationId 診察ID
     * @return レスポンスステータス（200）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/tickets/{ticketId}/examinations/{examinationId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String ticketId, @PathVariable String examinationId) {
        // クリニック権限のチェックとIDのコード体系チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(ticketId);
        ErrorsUtils.throwIfNotIdentify(examinationId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // チケットを更新する
        examinationService.deleteExaminationByExaminationId(clinicId, ticketId, examinationId);
        return ResponseEntity.ok().build();
    }
}
