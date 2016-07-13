package org.majimena.petical.batch.scraping.websites;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import org.apache.commons.lang3.StringUtils;
import org.majimena.petical.batch.runner.MedicineWrap;
import org.majimena.petical.batch.scraping.utils.HtmlUnitUtils;
import org.majimena.petical.batch.scraping.utils.NullableHashMap;
import org.majimena.petical.batch.scraping.utils.NvalConvertUtils;
import org.majimena.petical.batch.scraping.utils.NvalHtmlUnitUtils;
import org.majimena.petical.batch.scraping.utils.ScrapingUtils;
import org.majimena.petical.domain.Medicine;
import org.majimena.petical.domain.NvalItem;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import rx.Observable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 動物用医薬品等データベースウェブサイト.
 * <i>http://www.nval.go.jp/</i>
 */
public class NvalScraperImpl {

    private String startUrl = "http://www.nval.go.jp/asp/asp_dbDR_idx.asp";

    public static NvalItem parseDetail(HtmlPage htmlPage) {
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

        // データタイプをドメインモデルに変換する
        NvalItem medicine = new NvalItem();
        medicine.setName(NvalConvertUtils.getString(collect, "name"));
        medicine.setCategoryName(NvalConvertUtils.getString(collect, "categoryName"));
        medicine.setSideEffect(NvalConvertUtils.getBoolean(collect, "sideEffect"));
        medicine.setMedicinalEffectCategory(NvalConvertUtils.getString(collect, "medicinalEffectCategory"));
        medicine.setPackingUnit(NvalConvertUtils.getString(collect, "packingUnit"));
        medicine.setTarget(NvalConvertUtils.getString(collect, "target"));
        medicine.setBanningPeriod(NvalConvertUtils.getString(collect, "banningPeriod"));
        medicine.setEffect(NvalConvertUtils.getString(collect, "effect"));
        medicine.setDosage(NvalConvertUtils.getString(collect, "dosage"));
        medicine.setAttention(NvalConvertUtils.getString(collect, "attention"));
        medicine.setStorageCondition(NvalConvertUtils.getString(collect, "storageCondition"));
        medicine.setNote(NvalConvertUtils.getString(collect, "note"));
        medicine.setModifiedDate(NvalConvertUtils.getLocalDateTime(collect, "modifiedDate"));
        medicine.setApprovedDate(NvalConvertUtils.getLocalDateTime(collect, "approvedDate"));
        medicine.setApprovedDate1(NvalConvertUtils.getLocalDateTime(collect, "approvedDate1"));
        medicine.setApprovedDate2(NvalConvertUtils.getLocalDateTime(collect, "approvedDate2"));
        medicine.setApprovedDate3(NvalConvertUtils.getLocalDateTime(collect, "approvedDate3"));
        medicine.setNotifiedDate(NvalConvertUtils.getLocalDateTime(collect, "notifiedDate"));
        medicine.setReExamineResultNoticeDate(NvalConvertUtils.getLocalDateTime(collect, "reExamineResultNoticeDate"));
        medicine.setMakerOrDealerName(NvalConvertUtils.getString(collect, "makerOrDealerName"));
        medicine.setSelectedMakerOrDealerName(NvalConvertUtils.getString(collect, "selectedMakerOrDealerName"));
        medicine.setPreparationType(NvalConvertUtils.getString(collect, "preparationType"));
        medicine.setFormType(NvalConvertUtils.getString(collect, "formType"));
        medicine.setRegulationType(NvalConvertUtils.getString(collect, "regulationType"));
        medicine.setAvailablePeriod(NvalConvertUtils.getString(collect, "availablePeriod"));
        medicine.setRuminantByProducts(NvalConvertUtils.getString(collect, "ruminantByProducts"));
        return medicine;
    }

    // init -> toppage
    public HtmlPage topPage() throws IOException {
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

        HtmlPage page = client.getPage(startUrl);

        return page;
    }

    // toppage -> list
    public HtmlPage search(HtmlPage htmlPage) {
        // 最初に見つかったPOSTのFORMを取得する
        HtmlForm form = htmlPage.getForms().stream()
                .filter(htmlForm -> StringUtils.equals("post", htmlForm.getMethodAttribute().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> ScrapingUtils.newCannotScrapeException());

        // フォームからGOボタンを探し、クリックする
        HtmlPage listPage = form.getInputsByName("go").stream()
                .filter(input -> StringUtils.equals("button", input.getTypeAttribute().toLowerCase()))
                // 「go」ボタンが複数あると困るので、最初に見つかったものに絞る
                .findFirst()
                .map(input -> HtmlUnitUtils.click(input))
                .orElse(null);

        return listPage;
    }

    // list -> list
    public HtmlPage next(HtmlPage htmlPage) {
        // 次のページへ
        HtmlPage nextPage = htmlPage.getAnchors().stream()
                .filter(anchor -> NvalHtmlUnitUtils.equalAnchorText(anchor, "次の20件を表示する>>"))
                .findFirst()
                .map(anchor -> HtmlUnitUtils.click(anchor))
                .orElse(null);
        return nextPage;
    }

    // list -> detail(s)
    public void details(HtmlPage htmlPage, Consumer<HtmlPage> action) {
        // 検索結果ページから医薬品のテーブルだけ抜いて処理する
        HtmlUnitUtils.getHtmlTables(htmlPage)
                .filter(table -> table.getRow(0).getCells().size() == 5) // 項目数が５のテーブルに絞る
                .flatMap(table -> table.getRows().stream())
                .flatMap(row -> row.getCells().stream())
                .filter(cell -> HtmlAnchor.class.isAssignableFrom(cell.getFirstChild().getClass()))
                .map(cell -> HtmlAnchor.class.cast(cell.getFirstChild()))
                .flatMap(anchor -> Stream.of(HtmlUnitUtils.click(anchor)))
                .forEach(action);
    }

    public List<MedicineWrap> getMedicines(HtmlPage htmlPage) {
        // 検索結果ページから医薬品のテーブルだけ抜いて処理する
        return HtmlUnitUtils.getHtmlTables(htmlPage)
                .filter(table -> table.getRow(0).getCells().size() == 5) // 項目数が５のテーブルに絞る
                .flatMap(table -> table.getRows().stream())
                .flatMap(row -> row.getCells().stream())
                .filter(cell -> HtmlAnchor.class.isAssignableFrom(cell.getFirstChild().getClass()))
                .map(cell -> HtmlAnchor.class.cast(cell.getFirstChild()))
                .flatMap(anchor -> Stream.of(HtmlUnitUtils.click(anchor)))
                .flatMap(page -> {
                    NvalItem medicine = NvalScraper.parseDetail(page);
                    medicine.setId(UUID.randomUUID().toString());
                    MedicineWrap wrap = new MedicineWrap(medicine);
                    return Stream.of(wrap);
                })
                .collect(Collectors.toList());
    }
}
