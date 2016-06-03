package org.majimena.petical.batch.scraping.websites;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import org.majimena.petical.batch.scraping.utils.HtmlUnitUtils;
import org.majimena.petical.batch.scraping.utils.NullableHashMap;
import org.majimena.petical.batch.scraping.utils.NvalConvertUtils;
import org.majimena.petical.batch.scraping.utils.NvalHtmlUnitUtils;
import org.majimena.petical.batch.scraping.utils.ScrapingUtils;
import org.majimena.petical.domain.Medicine;
import org.apache.commons.lang3.StringUtils;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 動物用医薬品等データベースウェブサイト.
 * <i>http://www.nval.go.jp/</i>
 */
public class Nval {

    private String startUrl = "http://www.nval.go.jp/asp/asp_dbDR_idx.asp";

    public WebClient createWebClient() {
        // TODO ClientFactory
        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setJavaScriptEnabled(true);
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        client.waitForBackgroundJavaScript(10000);
        client.getOptions().setRedirectEnabled(true);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getCookieManager().setCookiesEnabled(true);
        return client;
    }

    public Observable<HtmlPage> init(WebClient client) {
        return Observable.create(subscriber -> {
            try {
                HtmlPage page = client.getPage(startUrl);
                subscriber.onNext(page);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    /**
     * 初回の検索処理（検索条件を指定してGOのボタン押下）を実行する.
     *
     * @param htmlPage 初回アクセスページ
     * @return 検索結果ページを持つオブザーバ
     */
    public Observable<HtmlPage> search(HtmlPage htmlPage) {
        return Observable.create(subscriber -> {
            try {
                // 最初に見つかったPOSTのFORMを取得する
                HtmlForm form = htmlPage.getForms().stream()
                        .filter(htmlForm -> StringUtils.equals("post", htmlForm.getMethodAttribute().toLowerCase()))
                        .findFirst()
                        .orElseThrow(() -> ScrapingUtils.newCannotScrapeException());

                // フォームからGOボタンを探し、クリックする
                form.getInputsByName("go").stream()
                        .filter(input -> StringUtils.equals("button", input.getTypeAttribute().toLowerCase()))
                        // 「go」ボタンが複数あると困るので、最初に見つかったものに絞る
                        .findFirst()
                        .map(input -> HtmlUnitUtils.click(input))
                        // 後続処理に結果を渡す
                        .ifPresent(page -> subscriber.onNext(page));

                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<HtmlPage> extract(HtmlPage htmlPage) {
        return Observable.create(subscriber -> {
            try {
                HtmlPage temp = htmlPage;

                while (true) {
                    // 検索結果ページから医薬品のテーブルだけ抜いて処理する
                    HtmlUnitUtils.getHtmlTables(temp)
                            .filter(table -> table.getRow(0).getCells().size() == 5) // 項目数が５のテーブルに絞る
                            .flatMap(table -> table.getRows().stream())
                            .flatMap(row -> row.getCells().stream())
                            .filter(cell -> HtmlAnchor.class.isAssignableFrom(cell.getFirstChild().getClass()))
                            .map(cell -> HtmlAnchor.class.cast(cell.getFirstChild()))
                            .flatMap(anchor -> Stream.of(HtmlUnitUtils.click(anchor)))
                            .forEach(page -> subscriber.onNext(page));

                    // 次のページへ
                    HtmlPage nextPage = temp.getAnchors().stream()
                            .filter(anchor -> NvalHtmlUnitUtils.equalAnchorText(anchor, "次の20件を表示する>>"))
                            .findFirst()
                            .map(anchor -> HtmlUnitUtils.click(anchor))
                            .orElse(null);
                    if (nextPage == null) {
                        break;
                    } else {
                        temp = nextPage;
                    }
//                    break;
                }

                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public void scrape(WebClient client) {
        Observable<HtmlPage> toppage = Observable.create(subscriber -> {
            HtmlPage page = null;
            try {
                page = client.getPage(startUrl);
                subscriber.onNext(page);
                subscriber.onCompleted();
            } catch (IOException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    public Observable<Medicine> format(HtmlPage htmlPage) {
        return detail(htmlPage).flatMap(map -> convert(map));
    }

    @Deprecated
    public Observable<Map<String, String>> detail(HtmlPage htmlPage) {
        return Observable.create(subscriber -> {
            try {
                // HTMLテーブルから詳細情報をパースして集める
                Map<String, String> collect = HtmlUnitUtils.getHtmlTables(htmlPage)
                        .flatMap(table -> {
                            List<HtmlTableRow> rows = table.getRows();
                            Map<String, String> map = new NullableHashMap<>();

                            for (HtmlTableRow row : rows) {
                                map.put("modifiedDate", NvalHtmlUnitUtils.getRightSideContent(row, "更新日"));
                                map.put("approvedType", NvalHtmlUnitUtils.getRightSideContent(row, "承認区分"));
                                map.put("approvedDate", NvalHtmlUnitUtils.getRightSideContent(row, "承認年月日"));
                                map.put("approvedDate1", NvalHtmlUnitUtils.getRightSideContent(row, "承認年月日1"));
                                map.put("approvedDate2", NvalHtmlUnitUtils.getRightSideContent(row, "承認年月日2"));
                                map.put("approvedDate3", NvalHtmlUnitUtils.getRightSideContent(row, "承認年月日3"));
                                map.put("notifiedDate", NvalHtmlUnitUtils.getRightSideContent(row, "届出年月日"));
                                map.put("reExamineResultNoticeDate", NvalHtmlUnitUtils.getRightSideContent(row, "再審査結果通知日"));
                                map.put("makerOrDealerName", NvalHtmlUnitUtils.getRightSideContent(row, "製造販売業者名"));
                                map.put("selectedMakerOrDealerName", NvalHtmlUnitUtils.getRightSideContent(row, "選任製造販売業者"));
                                map.put("preparationType", NvalHtmlUnitUtils.getRightSideContent(row, "製剤区分"));
                                map.put("formType", NvalHtmlUnitUtils.getRightSideContent(row, "剤型区分"));
                                map.put("regulationType", NvalHtmlUnitUtils.getRightSideContent(row, "規制区分"));
                                map.put("availablePeriod", NvalHtmlUnitUtils.getRightSideContent(row, "有効期間"));
                                map.put("ruminantByProducts", NvalHtmlUnitUtils.getRightSideContent(row, "反芻動物由来物質有無"));
                            }

                            map.put("name", NvalHtmlUnitUtils.getNextRowContent(rows, "商品名称"));
                            map.put("categoryName", NvalHtmlUnitUtils.getNextRowContent(rows, "一般的名称"));
                            map.put("sideEffect", NvalHtmlUnitUtils.getNextRowContent(rows, "副作用情報"));
                            map.put("medicinalEffectCategory", NvalHtmlUnitUtils.getNextRowContent(rows, "薬効分類"));
                            map.put("packingUnit", NvalHtmlUnitUtils.getNextRowContent(rows, "包装単位"));
                            map.put("target", NvalHtmlUnitUtils.getNextRowContent(rows, "対象動物"));
                            map.put("banningPeriod", NvalHtmlUnitUtils.getNextRowContent(rows, "使用禁止期間／休薬期間"));
                            map.put("effect", NvalHtmlUnitUtils.getNextRowContent(rows, "効能効果"));
                            map.put("dosage", NvalHtmlUnitUtils.getNextRowContent(rows, "用法用量"));
                            map.put("attention", NvalHtmlUnitUtils.getNextRowContent(rows, "使用上の注意"));
                            map.put("storageCondition", NvalHtmlUnitUtils.getNextRowContent(rows, "貯蔵方法"));
                            map.put("note", NvalHtmlUnitUtils.getNextRowContent(rows, "備考"));

                            // TODO 配列になるので、主成分と投与経路、由来物質をどうするかは検討必要
                            return map.entrySet().stream();
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                subscriber.onNext(collect);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Deprecated
    public Observable<Medicine> convert(Map<String, String> map) {
        return Observable.create(subscriber -> {
            try {
                // データタイプをドメインモデルに変換する
                Medicine medicine = new Medicine();
                medicine.setName(NvalConvertUtils.getString(map, "name"));
                medicine.setCategoryName(NvalConvertUtils.getString(map, "categoryName"));
                medicine.setSideEffect(NvalConvertUtils.getBoolean(map, "sideEffect"));
                medicine.setMedicinalEffectCategory(NvalConvertUtils.getString(map, "medicinalEffectCategory"));
                medicine.setPackingUnit(NvalConvertUtils.getString(map, "packingUnit"));
                medicine.setTarget(NvalConvertUtils.getString(map, "target"));
                medicine.setBanningPeriod(NvalConvertUtils.getString(map, "banningPeriod"));
                medicine.setEffect(NvalConvertUtils.getString(map, "effect"));
                medicine.setDosage(NvalConvertUtils.getString(map, "dosage"));
                medicine.setAttention(NvalConvertUtils.getString(map, "attention"));
                medicine.setStorageCondition(NvalConvertUtils.getString(map, "storageCondition"));
                medicine.setNote(NvalConvertUtils.getString(map, "note"));

                medicine.setModifiedDate(NvalConvertUtils.getLocalDateTime(map, "modifiedDate"));
                medicine.setApprovedDate(NvalConvertUtils.getLocalDateTime(map, "approvedDate"));
                medicine.setApprovedDate1(NvalConvertUtils.getLocalDateTime(map, "approvedDate1"));
                medicine.setApprovedDate2(NvalConvertUtils.getLocalDateTime(map, "approvedDate2"));
                medicine.setApprovedDate3(NvalConvertUtils.getLocalDateTime(map, "approvedDate3"));
                medicine.setNotifiedDate(NvalConvertUtils.getLocalDateTime(map, "notifiedDate"));
                medicine.setReExamineResultNoticeDate(NvalConvertUtils.getLocalDateTime(map, "reExamineResultNoticeDate"));
                medicine.setMakerOrDealerName(NvalConvertUtils.getString(map, "makerOrDealerName"));
                medicine.setSelectedMakerOrDealerName(NvalConvertUtils.getString(map, "selectedMakerOrDealerName"));
                medicine.setPreparationType(NvalConvertUtils.getString(map, "preparationType"));
                medicine.setFormType(NvalConvertUtils.getString(map, "formType"));
                medicine.setRegulationType(NvalConvertUtils.getString(map, "regulationType"));
                medicine.setAvailablePeriod(NvalConvertUtils.getString(map, "availablePeriod"));
                medicine.setRuminantByProducts(NvalConvertUtils.getString(map, "ruminantByProducts"));

                subscriber.onNext(medicine);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
