package org.majimena.petz.web.api.customer;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.domain.Chart;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ChartService;
import org.majimena.petz.service.CustomerService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * クリニック顧客カルテコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicCustomerChartController {

    /**
     * カルテサービス.
     */
    @Inject
    private ChartService chartService;

    /**
     * 顧客サービス.
     */
    @Inject
    private CustomerService customerService;

    /**
     * クリニックで管理しているこの顧客のカルテを取得する.
     *
     * @param clinicId   クリニックID
     * @param customerId 顧客ID
     * @return レスポンスエンティティ（通常時は200）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/customers/{customerId}/charts", method = RequestMethod.GET)
    public ResponseEntity<List<Chart>> get(@PathVariable String clinicId, @PathVariable String customerId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(customerId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // 参照可能な顧客であれば、その顧客のカルテを全て取得する
        List<Chart> charts = customerService.getCustomerByCustomerId(customerId)
                .filter(customer -> StringUtils.equals(clinicId, customer.getClinic().getId()))
                .map(customer -> chartService.getChartsByCustomerId(customer.getId()))
                .orElse(Arrays.asList());
        return ResponseEntity.ok().body(charts);
    }
}
