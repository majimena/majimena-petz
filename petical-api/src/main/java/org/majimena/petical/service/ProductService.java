package org.majimena.petical.service;

import org.majimena.petical.domain.Product;
import org.majimena.petical.domain.product.ProductCriteria;

import java.util.List;
import java.util.Optional;

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

    Optional<Product> getProductByProductId(String productId);

    Product saveProduct(Product product);

    Product updateProduct(Product product);

    void deleteProductByProductId(String clinicId, String productId);
}
