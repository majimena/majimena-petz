package org.majimena.petz.web.api.product;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ProductService;
import org.majimena.petz.web.utils.ErrorsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * プロダクトコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ProductController {

    /**
     * プロダクトサービス.
     */
    @Inject
    private ProductService productService;

    /**
     * プロダクトバリデータ.
     */
    @Inject
    private ProductValidator productValidator;

    /**
     * プロダクトサービスを設定する.
     *
     * @param productService プロダクトサービス
     */
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    /**
     * プロダクトバリデータを設定する.
     *
     * @param productValidator プロダクトバリデータ
     */
    public void setProductValidator(ProductValidator productValidator) {
        this.productValidator = productValidator;
    }

    /**
     * プロダクトを取得する.
     *
     * @param clinicId クリニックID
     * @return 該当するプロダクトの一覧
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/products", method = RequestMethod.GET)
    public ResponseEntity<List<Product>> get(@PathVariable String clinicId, @Valid ProductCriteria criteria) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // プロダクトを検索する
        criteria.setClinicId(clinicId);
        List<Product> list = productService.getProductsByProductCriteria(criteria);
        return ResponseEntity.ok().body(list);
    }

    /**
     * プロダクトを登録する.
     *
     * @param clinicId クリニックID
     * @param product  プロダクト
     * @param errors   エラー
     * @return レスポンスエンティティ（通常時は201）
     * @throws BindException バリデーションエラーがあった場合に発生する例外
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/products", method = RequestMethod.POST)
    public ResponseEntity<Product> post(@PathVariable String clinicId, @Valid @RequestBody Product product, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        productValidator.validate(product, errors);
        ErrorsUtils.throwIfHaveErrors(errors);

        // チケットを保存する
        Product saved = productService.saveProduct(product);
        return ResponseEntity.created(
                URI.create("/api/v1/clinics/" + clinicId + "/products/" + saved.getId())).body(saved);
    }
}
