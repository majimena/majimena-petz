package io.petical.batch.runner;

import io.petical.batch.scraping.websites.Nval;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by todoken on 2016/05/19.
 */
@EnableAutoConfiguration
@EnableBatchProcessing
@EnableConfigurationProperties
public class MedicineSynchronousRunner implements CommandLineRunner, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(MedicineSynchronousRunner.class);

    @Autowired
    private ApplicationContext context;
    @Autowired
    private Environment env;
    @Autowired
    private DataSource dataSource;

    private NamedParameterJdbcOperations namedParameterJdbcTemplate;
    private ItemSqlParameterSourceProvider itemSqlParameterSourceProvider;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MedicineSynchronousRunner.class);
        application.setWebEnvironment(false);
        ApplicationContext context = application.run();
        SpringApplication.exit(context);
    }

    @Override
    @Transactional
    public void run(String... strings) throws Exception {
        String sql = "INSERT INTO medicine (id, nval_id, name, category_name, side_effect, medicinal_effect_category, packing_unit, target, banning_period, effect, dosage, attention, storage_condition, note, modified_date, approved_date, approved_type, approved_date1, approved_date2, approved_date3, notified_date, re_examine_result_notice_date, maker_or_dealer_name, selected_maker_or_dealer_name, preparation_type, form_type, regulation_type, available_period, ruminant_by_products, created_by, created_date, last_modified_by, last_modified_date) " +
                "VALUES (:id, :nvalId, :name, :categoryName, :sideEffect, :medicinalEffectCategory, :packingUnit, :target, :banningPeriod, :effect, :dosage, :attention, :storageCondition, :note, :modifiedDate, :approvedDate, :approvedType, :approvedDate1, :approvedDate2, :approvedDate3, :notifiedDate, :reExamineResultNoticeDate, :makerOrDealerName, :selectedMakerOrDealerName, :preparationType, :formType, :regulationType, :availablePeriod, :ruminantByProducts, :createdBy, :createdDate, :lastModifiedBy, :lastModifiedDate)";

        final Nval sut = new Nval();
        sut.init()
                .flatMap(page -> sut.search(page))
                .flatMap(page -> sut.extract(page))
                .flatMap(page -> sut.format(page))
                .subscribe(medicine -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug(medicine.toString());
                    }
                    try {
                        SqlParameterSource source = itemSqlParameterSourceProvider.createSqlParameterSource(medicine);
                        int[] ints = namedParameterJdbcTemplate.batchUpdate(sql, new SqlParameterSource[]{source});
                        logger.info("medicineマスタを更新しました。" + ints.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
        this.itemSqlParameterSourceProvider = new BeanPropertyItemSqlParameterSourceProvider<>();
    }
}
