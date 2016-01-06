package org.majimena.petz.web.api.vaccine;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Vaccine;
import org.majimena.petz.domain.vaccine.VaccineCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.VaccineService;
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
import java.util.Optional;

/**
 * クリニック管理のワクチンに関するRESTコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicVaccineController {

    /**
     * ワクチンサービス.
     */
    @Inject
    private VaccineService vaccineService;

    /**
     * ワクチンバリデータ.
     */
    @Inject
    private VaccineValidator vaccineValidator;

    /**
     * ワクチンを取得する.
     *
     * @param clinicId クリニックID
     * @param criteria ワクチンクライテリア
     * @return レスポンスエンティティ（通常時は200、権限エラー時は401）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/vaccines", method = RequestMethod.GET)
    public ResponseEntity<List<Vaccine>> get(@PathVariable String clinicId, @Valid VaccineCriteria criteria) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // ワクチンを検索する
        criteria.setClinicId(clinicId);
        List<Vaccine> list = vaccineService.getVaccinesByVaccineCriteria(criteria);
        return ResponseEntity.ok().body(list);
    }

    /**
     * ワクチンを取得する.
     *
     * @param clinicId  クリニックID
     * @param vaccineId ワクチンID
     * @return レスポンスエンティティ（通常時は200、権限エラー時は401、結果がない場合は404）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/vaccines/{vaccineId}", method = RequestMethod.GET)
    public ResponseEntity<Vaccine> get(@PathVariable String clinicId, @PathVariable String vaccineId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(vaccineId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // ワクチンを検索してデータの権限をチェックする
        Optional<Vaccine> optional = vaccineService.getVaccineByVaccineId(vaccineId);
        optional.ifPresent(v -> SecurityUtils.throwIfDoNotHaveClinicRoles(v.getClinic().getId()));
        return optional.map(v -> ResponseEntity.ok().body(v))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * ワクチンを登録する.
     *
     * @param clinicId クリニックID
     * @param vaccine  ワクチン
     * @param errors   エラー
     * @return レスポンスエンティティ（通常時は201、権限エラー時は401）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは400）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/vaccines", method = RequestMethod.POST)
    public ResponseEntity<Vaccine> post(@PathVariable String clinicId, @Valid @RequestBody Vaccine vaccine, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        vaccine.setClinic(Clinic.builder().id(clinicId).build());
        vaccineValidator.validate(vaccine, errors);
        ErrorsUtils.throwIfHaveErrors(errors);

        // ワクチンを保存する
        Vaccine saved = vaccineService.saveVaccine(vaccine);
        return ResponseEntity.created(
            URI.create("/api/v1/clinics/" + clinicId + "/vaccines/" + saved.getId())).body(saved);
    }

    /**
     * ワクチンを更新する.
     *
     * @param clinicId  クリニックID
     * @param vaccineId ワクチンID
     * @param vaccine   ワクチン
     * @param errors    エラー
     * @return レスポンスエンティティ（通常時は200、権限エラー時は401）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは400）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/vaccines/{vaccineId}", method = RequestMethod.PUT)
    public ResponseEntity<Vaccine> put(@PathVariable String clinicId, @PathVariable String vaccineId,
                                       @Valid @RequestBody Vaccine vaccine, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(vaccineId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        vaccine.setId(vaccineId);
        vaccine.setClinic(Clinic.builder().id(clinicId).build());
        vaccineValidator.validate(vaccine, errors);
        ErrorsUtils.throwIfHaveErrors(errors);

        // ワクチンを保存する
        Vaccine saved = vaccineService.updateVaccine(vaccine);
        return ResponseEntity.ok().body(saved);
    }

    /**
     * ワクチンを削除する.
     *
     * @param clinicId  クリニックID
     * @param vaccineId ワクチンID
     * @return レスポンスエンティティ（通常時は200、権限エラー時は401、結果がない場合は404）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/vaccines/{vaccineId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String vaccineId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(vaccineId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // エラーにならなければ消しても良いということ
        Optional<Vaccine> optional = vaccineService.getVaccineByVaccineId(vaccineId);
        return optional
            .map(vaccine -> {
                SecurityUtils.throwIfUnmatchClinicId(clinicId, vaccine.getClinic().getId());
                vaccineService.deleteVaccine(vaccine);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
