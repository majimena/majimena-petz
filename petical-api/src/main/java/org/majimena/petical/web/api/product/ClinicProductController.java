package org.majimena.petical.web.api.product;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.Product;
import org.majimena.petical.domain.product.ProductCriteria;
import org.majimena.petical.security.SecurityUtils;
import org.majimena.petical.service.ProductService;
import org.majimena.petical.web.utils.ErrorsUtils;
import org.springframework.http.HttpStatus;
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
import java.util.Optional;

/**
 * プロダクトコントローラ.
 */
@RestController
@RequestMapping("/api/v1")
public class ClinicProductController {

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
     * @return レスポンスエンティティ（通常時は200）
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
     * プロダクトを取得する.
     *
     * @param clinicId  クリニックID
     * @param productId プロダクトID
     * @return レスポンスエンティティ（通常時は200だが、結果がない場合は404）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/products/{productId}", method = RequestMethod.GET)
    public ResponseEntity<Product> get(@PathVariable String clinicId, @PathVariable String productId) {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(productId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // プロダクトを検索してデータの権限をチェックする
        Optional<Product> result = productService.getProductByProductId(productId);
        result.ifPresent(p -> SecurityUtils.throwIfDoNotHaveClinicRoles(p.getClinic().getId()));
        return result.map(p -> ResponseEntity.ok().body(p))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * プロダクトを登録する.
     *
     * @param clinicId クリニックID
     * @param product  プロダクト
     * @param errors   エラー
     * @return レスポンスエンティティ（通常時は201）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは400）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/products", method = RequestMethod.POST)
    public ResponseEntity<Product> post(@PathVariable String clinicId, @Valid @RequestBody Product product, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        product.setClinic(Clinic.builder().id(clinicId).build());
        productValidator.validate(product, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // プロダクトを保存する
        Product saved = productService.saveProduct(product);
        return ResponseEntity.created(
                URI.create("/api/v1/clinics/" + clinicId + "/products/" + saved.getId())).body(saved);
    }

    /**
     * プロダクトを更新する.
     *
     * @param clinicId  クリニックID
     * @param productId プロダクトID
     * @param product   プロダクト
     * @param errors    エラー
     * @return レスポンスエンティティ（通常時は200）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは401）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/products/{productId}", method = RequestMethod.PUT)
    public ResponseEntity<Product> put(@PathVariable String clinicId, @PathVariable String productId,
                                       @Valid @RequestBody Product product, BindingResult errors) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(productId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // カスタムバリデーションを行う
        product.setId(productId);
        product.setClinic(Clinic.builder().id(clinicId).build());
        productValidator.validate(product, errors);
        ErrorsUtils.throwIfHasErrors(errors);

        // プロダクトを保存する
        Product saved = productService.updateProduct(product);
        return ResponseEntity.ok().body(saved);
    }

    /**
     * プロダクトを削除する.
     *
     * @param clinicId  クリニックID
     * @param productId プロダクトID
     * @return レスポンスエンティティ（通常時は200）
     * @throws BindException バリデーションエラーがあった場合に発生する例外（レスポンスコードは401）
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/products/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String clinicId, @PathVariable String productId) throws BindException {
        // クリニックの権限チェック
        ErrorsUtils.throwIfNotIdentify(clinicId);
        ErrorsUtils.throwIfNotIdentify(productId);
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // プロダクトを削除する
        productService.deleteProductByProductId(clinicId, productId);
        return ResponseEntity.ok().build();
    }
}
