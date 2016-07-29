package org.majimena.petical.web.api.clinics.inspections;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.ClinicInspection;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicInspectionService;
import org.majimena.petical.web.utils.ErrorsUtils;
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
 * 動物病院検査コントローラ.
 */
@RestController
@RequestMapping("/api/v1/clinics/{clinicId}/inspections")
public class ClinicInspectionController {

    @Inject
    private ClinicInspectionService clinicInspectionService;

    @Inject
    private ClinicInspectionValidator clinicInspectionValidator;

    /**
     * 動物病院診察料金を全て取得する.
     *
     * @param clinicId クリニックID
     * @return レスポンスエンティティ（通常時は200）
     */
    @Timed
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ClinicInspection>> get(@PathVariable String clinicId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 動物病院検査情報を全て取得
        List<ClinicInspection> list = clinicInspectionService.getClinicChargesByClinicId(clinicId);
        return ResponseEntity.ok().body(list);
    }

    /**
     * 動物病院診察料金を取得する.
     *
     * @param clinicId クリニックID
     * @param chargeId 動物病院診察料金ID
     * @return レスポンスエンティティ（通常時は200だが、結果がない場合は404）
     */
    @Timed
    @RequestMapping(value = "/{chargeId}", method = RequestMethod.GET)
    public ResponseEntity<ClinicInspection> get(@PathVariable String clinicId, @PathVariable String chargeId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(chargeId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // プロダクトを検索してデータの権限をチェックする
        return clinicInspectionService.getClinicChargeById(chargeId)
                .filter(charge -> SecurityUtils.isUserInClinic(charge.getClinic().getId()))
                .map(charge -> ResponseEntity.ok().body(charge))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 動物病院診察料金を登録する.
     *
     * @param clinicId クリニックID
     * @param charge   動物病院診察料金
     * @param errors   エラー
     * @return レスポンスエンティティ（通常時は201）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは400）
     */
    @Timed
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ClinicInspection> post(@PathVariable String clinicId, @Valid @RequestBody ClinicInspection charge, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        charge.setClinic(Clinic.builder().id(clinicId).build());
        clinicInspectionValidator.validate(charge, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // プロダクトを保存する
        ClinicInspection saved = clinicInspectionService.saveClinicCharge(charge);
        return ResponseEntity.created(
                URI.create("/api/v1/clinics/" + clinicId + "/charges/" + saved.getId())).body(saved);
    }

    /**
     * 動物病院診察料金を更新する.
     *
     * @param clinicId クリニックID
     * @param chargeId 動物病院診察料金ID
     * @param charge   動物病院診察料金
     * @param errors   エラー
     * @return レスポンスエンティティ（通常時は200）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは401）
     */
    @Timed
    @RequestMapping(value = "/{chargeId}", method = RequestMethod.PUT)
    public ResponseEntity<ClinicInspection> put(@PathVariable String clinicId, @PathVariable String chargeId,
                                                @Valid @RequestBody ClinicInspection charge, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(chargeId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        charge.setId(chargeId);
        charge.setClinic(Clinic.builder().id(clinicId).build());
        clinicInspectionValidator.validate(charge, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // プロダクトを保存する
        ClinicInspection saved = clinicInspectionService.updateClinicCharge(charge);
        return ResponseEntity.ok().body(saved);
    }

    /**
     * 動物病院診察料金を削除する.
     *
     * @param clinicId クリニックID
     * @param chargeId 動物病院診察料金ID
     * @return レスポンスエンティティ（通常時は200）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは401）
     */
    @Timed
    @RequestMapping(value = "/{chargeId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String chargeId) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(chargeId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // プロダクトを削除する
        return clinicInspectionService.getClinicChargeById(chargeId)
                .filter(charge -> SecurityUtils.isUserInClinic(charge.getClinic().getId()))
                .map(charge -> {
                    clinicInspectionService.removeClinicCharge(charge);
                    return ResponseEntity.ok().build();
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
