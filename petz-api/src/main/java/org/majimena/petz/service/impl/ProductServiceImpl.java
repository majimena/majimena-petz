package org.majimena.petz.service.impl;

import org.majimena.petz.common.utils.BeanFactoryUtils;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;
import org.majimena.petz.repository.ProductRepository;
import org.majimena.petz.repository.spec.ProductSpecs;
import org.majimena.petz.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * プロダクトサービスの実装.
 */
@Service
public class ProductServiceImpl implements ProductService {

    /**
     * プロダクトリポジトリ.
     */
    @Inject
    private ProductRepository productRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByProductCriteria(ProductCriteria criteria) {
        return productRepository.findAll(ProductSpecs.of(criteria));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductByProductId(String clinicId, String productId) {
        Product product = productRepository.findOne(productId);
        if (product != null) {
            // TODO クリニックの権限チェック
        }
        return Optional.ofNullable(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Product saveProduct(Product product) {
        Product saved = productRepository.save(product);
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Product updateProduct(Product product) {
        Product one = productRepository.findOne(product.getId());
        BeanFactoryUtils.copyNonNullProperties(product, one);

        Product saved = productRepository.save(one);
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteProductByProductId(String clinicId, String productId) {
        Product one = productRepository.findOne(productId);
        // TODO 存在チェックが足りていない

        // TODO クリニック権限チェックが足りていない

        // 論理削除する
        one.setRemoved(Boolean.TRUE);
        productRepository.save(one);
    }
}
