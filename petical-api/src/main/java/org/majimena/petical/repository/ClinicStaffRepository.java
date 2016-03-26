package org.majimena.petical.repository;

import org.majimena.petical.domain.Clinic;
import org.majimena.petical.domain.ClinicStaff;
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
    List<Clinic> findClinicsByUserId(@Param("userId") String userId);

    @Query("select cs from ClinicStaff cs inner join fetch cs.user us where cs.clinic.id = :clinicId order by cs.activatedDate")
    List<ClinicStaff> findByClinicId(@Param("clinicId") String clinicId);

    List<ClinicStaff> findByUserId(String userId);

    Optional<ClinicStaff> findByClinicIdAndUserId(String clinicId, String userId);

    void deleteByClinicId(String clinicId);
}

