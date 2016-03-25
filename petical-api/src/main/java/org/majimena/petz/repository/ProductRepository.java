package org.majimena.petz.repository;

import org.majimena.petz.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * プロダクトリポジトリ.
 */
public interface ProductRepository
        extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

}
