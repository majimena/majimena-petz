package org.majimena.petical.repository;

import org.majimena.petical.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * プロダクトリポジトリ.
 */
public interface ProductRepository
        extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

}
