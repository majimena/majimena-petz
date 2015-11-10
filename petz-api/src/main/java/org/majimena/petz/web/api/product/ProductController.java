package org.majimena.petz.web.api.product;

import com.codahale.metrics.annotation.Timed;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;
import org.majimena.petz.security.SecurityUtils;
import org.majimena.petz.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
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
     * プロダクトを取得する.
     *
     * @param clinicId クリニックID
     * @return 該当するプロダクトの一覧
     */
    @Timed
    @RequestMapping(value = "/clinics/{clinicId}/products", method = RequestMethod.GET)
    public ResponseEntity<List<Product>> get(@PathVariable String clinicId, @Valid ProductCriteria criteria) {
        // クリニックの権限チェック
        SecurityUtils.throwIfDoNotHaveClinicRoles(clinicId);

        // プロダクトを検索する
        criteria.setClinicId(clinicId);
        List<Product> list = productService.getProductsByProductCriteria(criteria);
        return ResponseEntity.ok().body(list);
    }
}
