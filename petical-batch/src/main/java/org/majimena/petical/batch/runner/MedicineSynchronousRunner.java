package org.majimena.petical.batch.runner;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.majimena.petical.batch.scraping.websites.NvalScraperImpl;
import org.majimena.petical.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FIXME もっとObservableを活用して、いい感じにかけるはず
 * Created by todoken on 2016/05/19.
 */
@SpringBootApplication
@EnableBatchProcessing
@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
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
    private long counter;
    private String sql = "INSERT INTO medicine (id, nval_id, name, category_name, side_effect, medicinal_effect_category, packing_unit, target, banning_period, effect, dosage, attention, storage_condition, note, modified_date, approved_date, approved_type, approved_date1, approved_date2, approved_date3, notified_date, re_examine_result_notice_date, maker_or_dealer_name, selected_maker_or_dealer_name, preparation_type, form_type, regulation_type, available_period, ruminant_by_products, created_by, created_date, last_modified_by, last_modified_date) " +
            "VALUES (:id, :nvalId, :name, :categoryName, :sideEffect, :medicinalEffectCategory, :packingUnit, :target, :banningPeriod, :effect, :dosage, :attention, :storageCondition, :note, :modifiedDate, :approvedDate, :approvedType, :approvedDate1, :approvedDate2, :approvedDate3, :notifiedDate, :reExamineResultNoticeDate, :makerOrDealerName, :selectedMakerOrDealerName, :preparationType, :formType, :regulationType, :availablePeriod, :ruminantByProducts, :createdBy, :createdDate, :lastModifiedBy, :lastModifiedDate)";
//    private String sql = "INSERT INTO medicine (id, nval_id, name, category_name, side_effect, medicinal_effect_category, packing_unit, target, banning_period, effect, dosage, attention, storage_condition, note, modified_date, approved_date, approved_type, approved_date1, approved_date2, approved_date3, notified_date, re_examine_result_notice_date, maker_or_dealer_name, selected_maker_or_dealer_name, preparation_type, form_type, regulation_type, available_period, ruminant_by_products, created_by, created_date, last_modified_by, last_modified_date) " +
//            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MedicineSynchronousRunner.class);
        application.setWebEnvironment(false);

        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        addDefaultProfile(application, source);

        ApplicationContext context = application.run();
        SpringApplication.exit(context);
    }

    private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        // SpringConfigが設定されていない場合は、開発モードで起動する
        if (!source.containsProperty("spring.profiles.active") && !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {
            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        }
    }

    // FIXME そもそもautocommit=trueになっている。。
    @Override
    public void run(String... strings) throws Exception {
        // １回目
//        final Nval sut = new Nval();
//        WebClient client = sut.createWebClient();
//        sut.init(client)
//                .flatMap(page -> sut.search(page))
//                .flatMap(page -> sut.extract(page))
//                .flatMap(page -> sut.format(page))
//                .subscribe(medicine -> {
//                    try {
//                        medicine.setId(UUID.randomUUID().toString());
//                        MedicineWrap wrap = new MedicineWrap(medicine);
//                        SqlParameterSource source = itemSqlParameterSourceProvider.createSqlParameterSource(wrap);
//                        namedParameterJdbcTemplate.batchUpdate(sql, new SqlParameterSource[]{source});
//                    } catch (Exception e) {
//                        logger.warn("failed data is " + medicine.toString(), e);
//                    }
//                })
//                .unsubscribe();

        // ２回目
//        NvalScraper scraper = new NvalScraper();
//        // 初回検索
//        Observable<HtmlPage> listPage = scraper.init()
//                .flatMap(htmlPage -> scraper.search(htmlPage));
//        // 抽出して保存
//        extract(listPage);

        // ３回目（普通に書いてみる）
        NvalScraperImpl scraper = new NvalScraperImpl();
        HtmlPage toppage = scraper.topPage();
        HtmlPage list = scraper.search(toppage);

        while (true) {
//            scraper.details(list, htmlPage -> {
//                Medicine org = NvalScraper.parseDetail(htmlPage);
//                try {
//                    org.setId(UUID.randomUUID().toString());
//                    MedicineWrap medicine = new MedicineWrap(org);
//                    this.jdbcTemplate.update(sql, ps -> {
//                        ps.setString(1, medicine.getId());
//                        ps.setString(2, medicine.getNvalId());
//                        ps.setString(3, medicine.getName());
//                        ps.setString(4, medicine.getCategoryName());
//                        ps.setBoolean(5, medicine.getSideEffect());
//                        ps.setString(6, medicine.getMedicinalEffectCategory());
//                        ps.setString(7, medicine.getPackingUnit());
//                        ps.setString(8, medicine.getTarget());
//                        ps.setString(9, medicine.getBanningPeriod());
//                        ps.setString(10, medicine.getEffect());
//                        ps.setString(11, medicine.getDosage());
//                        ps.setString(12, medicine.getAttention());
//                        ps.setString(13, medicine.getStorageCondition());
//                        ps.setString(14, medicine.getNote());
//                        ps.setTimestamp(15, medicine.getModifiedDate());
//                        ps.setTimestamp(16, medicine.getApprovedDate());
//                        ps.setString(17, medicine.getApprovedType());
//                        ps.setTimestamp(18, medicine.getApprovedDate1());
//                        ps.setTimestamp(19, medicine.getApprovedDate2());
//                        ps.setTimestamp(20, medicine.getApprovedDate3());
//                        ps.setTimestamp(21, medicine.getNotifiedDate());
//                        ps.setTimestamp(22, medicine.getReExamineResultNoticeDate());
//                        ps.setString(23, medicine.getMakerOrDealerName());
//                        ps.setString(24, medicine.getSelectedMakerOrDealerName());
//                        ps.setString(25, medicine.getPreparationType());
//                        ps.setString(26, medicine.getFormType());
//                        ps.setString(27, medicine.getRegulationType());
//                        ps.setString(28, medicine.getAvailablePeriod());
//                        ps.setString(29, medicine.getRuminantByProducts());
//                        ps.setString(30, medicine.getCreatedBy());
//                        ps.setTimestamp(31, medicine.getCreatedDate());
//                        ps.setString(32, medicine.getLastModifiedBy());
//                        ps.setTimestamp(33, medicine.getLastModifiedDate());
//                    });
//                    MedicineWrap wrap = new MedicineWrap(medicine);
//                    SqlParameterSource source = itemSqlParameterSourceProvider.createSqlParameterSource(wrap);
//                    namedParameterJdbcTemplate.update(sql, source);
//                    counter++;
//                    logger.info("[" + counter + "]件処理しました。");
//                } catch (Exception e) {
//                    logger.warn("failed data is " + org.toString(), e);
//                }
//            });

            try {
                List<MedicineWrap> medicines = scraper.getMedicines(list);
                SqlParameterSource[] sources = medicines.stream()
                        .map(wrap -> itemSqlParameterSourceProvider.createSqlParameterSource(wrap))
                        .collect(Collectors.toList())
                        .toArray(new SqlParameterSource[]{});

                namedParameterJdbcTemplate.batchUpdate(sql, sources);
                counter++;
                logger.info("[" + counter + "]件処理しました。");
            } catch (Exception e) {
                logger.error("", e);
            }

            list = scraper.next(list);
            if (list == null) {
                break;
            }
        }
    }

    private JdbcTemplate jdbcTemplate;
    @Override
    public void afterPropertiesSet() throws Exception {
//        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
        this.itemSqlParameterSourceProvider = new BeanPropertyItemSqlParameterSourceProvider<>();
    }

//    public void extract(Observable<HtmlPage> observable) {
//
//        // 小ページを探してあれば再帰的に処理
//        observable
//                .map(htmlPage -> {
//                    // 次のページへ
//                    HtmlPage nextPage = htmlPage.getAnchors().stream()
//                            .filter(anchor -> NvalHtmlUnitUtils.equalAnchorText(anchor, "次の20件を表示する>>"))
//                            .findFirst()
//                            .map(anchor -> HtmlUnitUtils.click(anchor))
//                            .orElse(null);
//                    return nextPage;
//                })
//                .observeOn(Schedulers.io())
//                .subscribe(nextPage -> {
//                    // 再帰的に処理する
//                    if (nextPage != null) {
//                        extract(Observable.just(nextPage).share());
//                    }
//                });
//
//        // 現ページをパースしてデータベースに登録
//        observable
//                .subscribe(htmlPage -> {
//                    // 検索結果ページから医薬品のテーブルだけ抜いて処理する
//                    HtmlUnitUtils.getHtmlTables(htmlPage)
//                            .filter(table -> table.getRow(0).getCells().size() == 5) // 項目数が５のテーブルに絞る
//                            .flatMap(table -> table.getRows().stream())
//                            .flatMap(row -> row.getCells().stream())
//                            .filter(cell -> HtmlAnchor.class.isAssignableFrom(cell.getFirstChild().getClass()))
//                            .map(cell -> HtmlAnchor.class.cast(cell.getFirstChild()))
//                            .flatMap(anchor -> Stream.of(HtmlUnitUtils.click(anchor)))
//                            .forEach(page -> {
//                                Medicine medicine = NvalScraper.parseDetail(page);
//                                try {
//                                    medicine.setId(UUID.randomUUID().toString());
//                                    MedicineWrap wrap = new MedicineWrap(medicine);
//                                    SqlParameterSource source = itemSqlParameterSourceProvider.createSqlParameterSource(wrap);
//                                    namedParameterJdbcTemplate.batchUpdate(sql, new SqlParameterSource[]{source});
//                                } catch (Exception e) {
//                                    logger.warn("failed data is " + medicine.toString(), e);
//                                }
//                                counter++;
//                                logger.info("[" + counter + "]件処理しました。");
//                            });
//                })
//                .unsubscribe();
//    }
}
