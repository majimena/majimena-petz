package io.petical.batch.configs;

import io.petical.batch.ObservableItemWriter;
import io.petical.batch.processors.MedicineItemProcessor;
import io.petical.batch.scraping.websites.Nval;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.majimena.petical.domain.Medicine;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rx.Observable;

/**
 * Created by todoken on 2016/05/21.
 */
//@Configuration
//@EnableBatchProcessing
public class MedicineBatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public DataSource dataSource;

    @Bean
    public ItemReader<Observable<Medicine>> medicineItemReader() {
        return () -> {
            Nval sut = new Nval();
            return sut.init()
                    .flatMap(page -> sut.search(page))
                    .flatMap(page -> sut.extract(page))
                    .flatMap(page -> sut.format(page));
        };
    }

    @Bean
    public MedicineItemProcessor medicineItemProcessor() {
        return new MedicineItemProcessor();
    }

    @Bean
    public ObservableItemWriter writer() {
        ObservableItemWriter writer = new ObservableItemWriter();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO medicine (id, nval_id, name, category_name, side_effect, medicinal_effect_category, packing_unit, target, banning_period, effect, dosage, attention, storage_condition, note, modified_date, approved_date, approved_type, approved_date1, approved_date2, approved_date3, notified_date, re_examine_result_notice_date, maker_or_dealer_name, selected_maker_or_dealer_name, preparation_type, form_type, regulation_type, available_period, ruminant_by_products, created_by, created_date, last_modified_by, last_modified_date) " +
                "VALUES (:id, :nvalId, :name, :categoryName, :sideEffect, :medicinalEffectCategory, :packingUnit, :target, :banningPeriod, :effect, :dosage, :attention, :storageCondition, :note, :modifiedDate, :approvedDate, :approvedType, :approvedDate1, :approvedDate2, :approvedDate3, :notifiedDate, :reExamineResultNoticeDate, :makerOrDealerName, :selectedMakerOrDealerName, :preparationType, :formType, :regulationType, :availablePeriod, :ruminantByProducts, :createdBy, :createdDate, :lastModifiedBy, :lastModifiedDate)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Job importMedicineJob() {
        return jobBuilderFactory.get("importMedicineJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Observable<Medicine>, Observable<Medicine>>chunk(10)
                .reader(medicineItemReader())
                .processor(medicineItemProcessor())
                .writer(writer())
                .build();
    }
}
