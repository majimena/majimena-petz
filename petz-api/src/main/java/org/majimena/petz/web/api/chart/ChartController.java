package org.majimena.petz.web.api.chart;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.domain.Chart;
import org.majimena.petz.domain.chart.ChartCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ChartService;
import org.majimena.petz.web.rest.util.PaginationUtil;
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
public class ChartController {

    /**
     * カルテサービス.
     */
    @Inject
    private ChartService chartService;

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
    public ResponseEntity<List<Chart>> getAll(@PathVariable String clinicId,
                                              @RequestParam(value = "page", required = false) Integer offset,
                                              @RequestParam(value = "per_page", required = false) Integer limit,
                                              @Valid ChartCriteria criteria) throws URISyntaxException {
        // クリニックの権限チェック
        if (!SecurityUtils.isUserInClinic(clinicId)) {
            throw new ResourceCannotAccessException(); // FIXME メッセージ詰める
        }

        Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
        criteria.setClinicId(clinicId);

        // カルテを検索する
        Page<Chart> charts = chartService.findChartsByChartCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(charts, "/api/v1/clinics/" + clinicId + "/charts", offset, limit);
        return new ResponseEntity<>(charts.getContent(), headers, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/charts/{chartId}", method = RequestMethod.GET)
    public ResponseEntity<Chart> show(@PathVariable String clinicId, @PathVariable String chartId) {
        // クリニックの権限チェック
        if (!SecurityUtils.isUserInClinic(clinicId)) {
            throw new ResourceCannotAccessException(); // FIXME メッセージ詰める
        }

        // カルテを取得する
        Optional<Chart> one = chartService.getChartByChartId(chartId);
        return one
                .map(chart -> {
                    if (!StringUtils.equals(clinicId, chart.getClinic().getId())) {
                        throw new ResourceCannotAccessException(); // FIXME メッセージ詰める
                    }
                    return ResponseEntity.ok().body(chart);
                })
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
    public ResponseEntity<Chart> post(@PathVariable String clinicId,
                                      @RequestBody @Valid Chart chart, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        if (!SecurityUtils.isUserInClinic(clinicId)) {
            throw new ResourceCannotAccessException(); // FIXME メッセージ詰める
        }

        // カルテを保存する
        Chart saved = chartService.saveChart(clinicId, chart);
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
    public ResponseEntity<Chart> put(@PathVariable String clinicId, @PathVariable String chartId,
                                     @RequestBody @Valid Chart chart, BindingResult errors) throws BindException {
        ResponseEntity<Chart> post = post(clinicId, chart, errors);
        return ResponseEntity.ok().body(post.getBody());
    }
}
