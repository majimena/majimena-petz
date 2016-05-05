package org.majimena.petical.web.api.clinic;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.client.recaptcha.RecaptchaRestAdapterFactory;
import org.majimena.petical.client.recaptcha.SiteVerifyApi;
import org.majimena.petical.client.recaptcha.SiteVerifyEntity;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.clinic.ClinicCriteria;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ClinicService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.majimena.petical.web.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * クリニックコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicController {

    /**
     * クリニックサービス.
     */
    @Inject
    private ClinicService clinicService;

    /**
     * クリニックバリデータ.
     */
    @Inject
    private ClinicValidator clinicValidator;

    /**
     * シークレットキー.
     */
    @Value("${google.recaptcha.secret:xxxxxx}")
    private String secret;

    // 今のところは使用しないAPI
    @Timed
    @RequestMapping(value = "/clinics", method = RequestMethod.GET)
    public ResponseEntity<List<Clinic>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                               @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
        Pageable pageable = PaginationUtils.generatePageRequest(offset, limit);
        Page<Clinic> page = clinicService.findClinicsByClinicCriteria(new ClinicCriteria(), pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(page, "/api/v1/clinics", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    // 今のところは使用しないAPI
    @Timed
    @RequestMapping(value = "/clinics/{id}", method = RequestMethod.GET)
    public ResponseEntity<Clinic> get(@PathVariable String id) {
        Optional<Clinic> one = clinicService.getClinicById(id);
        return one
                .map(clinic -> new ResponseEntity<>(clinic, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * クリニックを登録する.
     *
     * @param clinic クリニック
     * @return レスポンスエンティティ（通常時は201、入力エラー時は400）
     */
    @Timed
    @RequestMapping(value = "/clinics", method = RequestMethod.POST)
    public ResponseEntity<Clinic> post(@RequestParam String captcha, @Valid @RequestBody Clinic clinic, BindingResult errors) throws BindException {
        // キャプチャのバリデーション
        SiteVerifyEntity entity = RecaptchaRestAdapterFactory.create()
                .create(SiteVerifyApi.class)
                .post(this.secret, captcha, "");
        if (!entity.isSuccess()) {
            ErrorsUtils.reject(ErrorCode.PTZ_999981, errors);
        }

        // カスタムバリデータを行う
        clinicValidator.validate(clinic, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // クリニックを登録する
        Clinic saved = clinicService.saveClinic(clinic);
        return ResponseEntity.created(URI.create("/api/v1/clinics/" + saved.getId())).body(saved);
    }

    /**
     * クリニックを変更する.
     *
     * @param clinicId クリニックID
     * @param clinic   クリニック
     * @return レスポンスエンティティ（通常時は200、入力エラー時は400）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}", method = RequestMethod.PUT)
    public ResponseEntity<Clinic> put(@PathVariable String clinicId, @Valid @RequestBody Clinic clinic, BindingResult errors) throws BindException {
        // クリニックの権限チェックをする
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデータを行う
        clinic.setId(clinicId);
        clinicValidator.validate(clinic, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // クリニック情報を変更する
        Clinic save = clinicService.updateClinic(clinic);
        return ResponseEntity.ok().body(save);
    }

    /**
     * クリニックを削除する.<br/>
     * ただし、論理削除に止めるため、実際には削除されない.
     *
     * @param clinicId クリニックID
     * @return レスポンスエンティティ（通常時は200）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId) {
        // クリニックの権限チェックをする
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // クリニックを削除する
        clinicService.deleteClinic(clinicId);
        return ResponseEntity.ok().build();
    }
}
