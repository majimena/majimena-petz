package org.majimena.petz.repository;

import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * クリニックスタッフリポジトリ.
 */
public interface ClinicStaffRepository extends JpaRepository<ClinicStaff, String> {

    @Query("select cl from ClinicStaff cs inner join cs.clinic cl where cs.user.id = :userId order by cl.name, cl.id")
    Page<Clinic> findClinicsByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("select cs from ClinicStaff cs inner join fetch cs.user us where cs.clinic.id = :clinicId order by cs.activatedDate")
    List<ClinicStaff> findByClinicId(@Param("clinicId") String clinicId);

    Optional<ClinicStaff> findByClinicIdAndUserId(String clinicId, String userId);

    void deleteByClinicId(String clinicId);
}

