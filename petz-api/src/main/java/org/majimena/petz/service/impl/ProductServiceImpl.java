package org.majimena.petz.service.impl;

import org.majimena.petz.domain.Product;
import org.majimena.petz.domain.product.ProductCriteria;
import org.majimena.petz.repository.ProductRepository;
import org.majimena.petz.repository.spec.ProductSpecs;
import org.majimena.petz.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * プロダクトサービスの実装.
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Inject
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByProductCriteria(ProductCriteria criteria) {
        return productRepository.findAll(ProductSpecs.of(criteria));
    }
}
