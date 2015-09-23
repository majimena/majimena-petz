package org.majimena.petz.service.impl;

import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.common.exceptions.SystemException;
import org.majimena.petz.domain.Chart;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.Customer;
import org.majimena.petz.domain.Pet;
import org.majimena.petz.domain.chart.ChartCriteria;
import org.majimena.petz.repository.ChartRepository;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.CustomerRepository;
import org.majimena.petz.repository.spec.ChartCriteriaSpec;
import org.majimena.petz.repository.spec.ChartSpecs;
import org.majimena.petz.service.ChartService;
import org.majimena.petz.service.PetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * カルテサービスの実装クラス.
 */
@Component
public class ChartServiceImpl implements ChartService {

    /**
     * カルテリポジトリ.
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
     * ペットサービス.
     */
    @Inject
    private PetService petService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Chart> findChartsByChartCriteria(ChartCriteria criteria, Pageable pageable) {
        Page<Chart> charts = chartRepository.findAll(new ChartCriteriaSpec(criteria), pageable);
        // lazy load for relational entities
        charts.forEach(c -> {
            c.getClinic().getId();
            c.getCustomer().getId();
            c.getPet().getId();
        });
        return charts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Chart> getChartByChartId(String chartId) {
        Chart one = chartRepository.findOne(chartId);
        Optional<Chart> chart = Optional.ofNullable(one);
        // lazy load for relational entities if chart is exists
        chart.ifPresent(c -> {
            c.getClinic().getId();
            c.getCustomer().getId();
            c.getPet().getId();
        });
        return chart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Chart saveChart(String clinicId, Chart chart) throws ApplicationException {
        // カルテの保存に必要な関連情報を取得する
        Clinic clinic = clinicRepository.findOne(clinicId);
        Customer customer = customerRepository.findOne(chart.getCustomer().getId());
        if (clinic == null || customer == null) {
            throw new SystemException("must be found clinic [" + clinicId + "] and customer [" + chart.getCustomer().getId() + "].");
        }

        // ペット情報が変更されているかもしれないので、ユーザーを指定して保存する
        chart.getPet().setUser(customer.getUser());
        Pet pet = petService.savePet(chart.getPet());

        // カルテを保存する
        chart.setChartNo(String.valueOf(System.currentTimeMillis()));
        chart.setClinic(clinic);
        chart.setCustomer(customer);
        chart.setPet(pet);
        chart.setCreationDate(LocalDateTime.now());
        return chartRepository.save(chart);
    }
}
