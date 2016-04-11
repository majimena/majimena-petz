package org.majimena.petical.web.api.chart;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.Chart;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.chart.ChartCriteria;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ChartService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.majimena.petical.web.utils.PaginationUtils;
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
 * カルテコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicChartController {

    /**
     * カルテサービス.
     */
    @Inject
    private ChartService chartService;

    /**
     * カルテバリデータ.
     */
    @Inject
    private ChartValidator chartValidator;

    /**
     * カルテを検索する.
     *
     * @param clinicId クリニックID
     * @param offset   検索時のオフセット値
     * @param limit    検索結果数の上限値
     * @param criteria 検索条件
     * @return 検索結果
     * @throws URISyntaxException URIエラー
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/charts", method = RequestMethod.GET)
    public ResponseEntity<List<Chart>> getAll(@PathVariable String clinicId, @Valid ChartCriteria criteria,
                                              @RequestParam(value = "page", required = false) Integer offset,
                                              @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カルテを検索する
        Pageable pageable = PaginationUtils.generatePageRequest(offset, limit);
        criteria.setClinicId(clinicId);
        Page<Chart> charts = chartService.findChartsByChartCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(charts, "/api/v1/clinics/" + clinicId + "/charts", offset, limit, criteria);
        return new ResponseEntity<>(charts.getContent(), headers, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/charts/{chartId}", method = RequestMethod.GET)
    public ResponseEntity<Chart> show(@PathVariable String clinicId, @PathVariable String chartId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(chartId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カルテを取得する
        Optional<Chart> one = chartService.getChartByChartId(clinicId, chartId);
        return one
                .map(chart -> ResponseEntity.ok().body(chart))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 自分のクリニックに新規のカルテを登録する.
     *
     * @param clinicId クリニックID
     * @param chart    カルテ情報
     * @param errors   エラーオブジェクト
     * @return 登録結果
     * @throws BindException バリデーションエラーがある場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/charts", method = RequestMethod.POST)
    public ResponseEntity<Chart> post(@PathVariable String clinicId, @RequestBody @Valid Chart chart, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);
        chart.setClinic(Clinic.builder().id(clinicId).build());

        // 拡張バリデータを実施
        chartValidator.validate(chart, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // カルテを保存する
        Chart saved = chartService.saveChart(chart);
        return ResponseEntity.created(
                URI.create("/api/v1/clinics/" + clinicId + "/charts/" + saved.getId())).body(saved);
    }

    /**
     * 自分のクリニックのカルテを変更する.
     *
     * @param clinicId クリニックID
     * @param chartId  カルテID
     * @param chart    カルテ情報
     * @param errors   エラーオブジェクト
     * @return 登録結果
     * @throws BindException バリデーションエラーがある場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/charts/{chartId}", method = RequestMethod.PUT)
    public ResponseEntity<Chart> put(@PathVariable String clinicId, @PathVariable String chartId, @RequestBody @Valid Chart chart, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(chartId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 拡張バリデータを実施
        chartValidator.validate(chart, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // カルテを更新する
        chart.setClinic(Clinic.builder().id(clinicId).build());
        Chart saved = chartService.updateChart(chart);
        return ResponseEntity.ok().body(saved);
    }

    /**
     * 自分のクリニックのカルテを削除する.
     *
     * @param clinicId クリニックID
     * @param chartId  カルテID
     * @return レスポンスエンティティ（通常時は200、認証失敗時は401、対象が見つからない場合は404）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/charts/{chartId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> put(@PathVariable String clinicId, @PathVariable String chartId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(chartId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カルテを削除する
        return chartService.getChartByChartId(clinicId, chartId)
                .filter(chart -> StringUtils.equals(clinicId, chart.getClinic().getId()))
                .map(chart -> {
                    chartService.deleteChart(chart);
                    return ResponseEntity.ok().build();
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
