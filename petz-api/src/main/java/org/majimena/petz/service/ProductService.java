package org.majimena.petz.service;

import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;

import java.util.List;

/**
 * プロダクトサービス.
 */
public interface ProductService {

    /**
     * プロダクトクライテリアをもとに、プロダクトを検索する.
     *
     * @param criteria プロダクトクライテリア
     * @return 該当するプロダクトの一覧
     */
    List<Product> getProductsByProductCriteria(ProductCriteria criteria);

    Product saveProduct(Product product);

    Product updateProduct(Product product);

    void deleteProductByProductId(String clinicId, String productId);
}
