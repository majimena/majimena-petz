package org.majimena.petz.service.impl;

import org.majimena.petz.common.utils.BeanFactoryUtils;
import org.majimena.petz.common.utils.ExceptionUtils;
import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;
import org.majimena.petz.repository.ProductRepository;
import org.majimena.petz.repository.spec.ProductSpecs;
import org.majimena.petz.security.SecurityUtils;
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
    public Optional<Product> getProductByProductId(String productId) {
        Product product = productRepository.findOne(productId);
        return Optional.ofNullable(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Product saveProduct(Product product) {
        product.setRemoved(Boolean.FALSE);
        return productRepository.save(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Product updateProduct(Product product) {
        Product one = productRepository.findOne(product.getId());
        ExceptionUtils.throwIfNull(one);

        BeanFactoryUtils.copyNonNullProperties(product, one);
        return productRepository.save(one);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteProductByProductId(String clinicId, String productId) {
        // 削除対象が存在しなければ何もしない
        Product one = productRepository.findOne(productId);
        if (one == null) {
            return;
        }

        // 削除して良いクリニックかどうかチェックする
        SecurityUtils.throwIfUnmatchClinicId(clinicId, one.getClinic().getId());

        // 論理削除する
        one.setRemoved(Boolean.TRUE);
        productRepository.save(one);
    }
}
