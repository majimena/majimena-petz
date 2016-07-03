package org.majimena.petical.repository;

import org.majimena.petical.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * プロダクトリポジトリ.
 */
@Deprecated
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @Query(value = "insert into product "
            + "select uuid(), :clinicId, course, category, name, price, insurance, unit, tax_type, tax_rate, description, false, :userId, now(), :userId, now() "
            + "from product_base order by id", nativeQuery = true)
    void setup(@Param("clinicId") String clinicId, @Param("userId") String userId);

}
