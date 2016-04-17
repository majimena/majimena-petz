package org.majimena.petical.web.api.chart;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.domain.Chart;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.Customer;
import org.majimena.petical.domain.errors.ErrorCode;
import org.majimena.petical.repository.ChartRepository;
import org.majimena.petical.repository.ClinicRepository;
import org.majimena.petical.repository.CustomerRepository;
import org.majimena.petical.web.api.AbstractValidator;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

/**
 * 顧客バリデータ.
 */
@Named("chartValidator")
public class ChartValidator extends AbstractValidator<Chart> {

    /**
     * プロダクトリポジトリ.
     */
    @Inject
    private ChartRepository chartRepository;

    /**
     * クリニックリポジトリ.
     */
    @Inject
    private ClinicRepository clinicRepository;

    /**
     * 顧客リポジトリ.
     */
    @Inject
    private CustomerRepository customerRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Optional<Chart> target, Errors errors) {
        target.ifPresent(chart -> {
            // IDの存在チェック
            validateChartId(Optional.ofNullable(chart.getId()), chart.getClinic().getId(), errors);

            // クリニックの存在チェック
            Clinic clinic = validateClinicId(chart.getClinic().getId(), errors);
            chart.setClinic(clinic);

            // 顧客コードの存在チェック
            if (StringUtils.isEmpty(chart.getCustomer().getId())) {
                Customer customer = validateCustomerCode(chart.getClinic().getId(), chart.getCustomer().getCustomerCode(), errors);
                chart.setCustomer(customer);
            } else {
                Customer customer = validateCustomerId(clinic.getId(), chart.getCustomer().getId());
                chart.setCustomer(customer);
            }

            // カルテ番号の未存在チェック
            if (StringUtils.isEmpty(chart.getId())) {
                validateChartNo(chart.getClinic().getId(), chart.getChartNo(), errors);
            }
        });
    }

    private void validateChartId(Optional<String> value, String clinicId, Errors errors) {
        value.ifPresent(id -> {
            Chart one = chartRepository.findOne(id);
            if (one == null) {
                ErrorsUtils.reject(ErrorCode.PTZ_999998, errors);
            } else {
                ErrorsUtils.throwIfNotEqual(clinicId, one.getClinic().getId());
            }
        });
    }

    private Clinic validateClinicId(String clinicId, Errors errors) {
        Clinic one = clinicRepository.findOne(clinicId);
        if (one == null) {
            ErrorsUtils.rejectValue("clinic", ErrorCode.PTZ_001999, errors);
        }
        return one;
    }

    private Customer validateCustomerId(String clinicId, String customerId) {
        Customer one = customerRepository.findOne(customerId);
        if (!StringUtils.equals(clinicId, one.getClinic().getId())) {
            throw new IllegalArgumentException();
        }
        return one;
    }

    private Customer validateCustomerCode(String clinicId, String customerCode, Errors errors) {
        return customerRepository.findByClinicIdAndCustomerCode(clinicId, customerCode)
                .orElseGet(() -> {
                    ErrorsUtils.rejectValue("customer", ErrorCode.PTZ_003999, errors);
                    return null;
                });
    }

    private void validateChartNo(String clinicId, String chartNo, Errors errors) {
        chartRepository.findByClinicIdAndChartNo(clinicId, chartNo)
                .map(chart -> {
                    ErrorsUtils.rejectValue("chartNo", ErrorCode.PTZ_004001, errors);
                    return null;
                });
    }
}
