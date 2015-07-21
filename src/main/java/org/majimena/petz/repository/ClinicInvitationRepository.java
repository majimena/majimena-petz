package org.majimena.petz.repository;

import org.majimena.petz.domain.ClinicInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * クリニックスタッフ招待リポジトリ.
 */
public interface ClinicInvitationRepository extends JpaRepository<ClinicInvitation, String> {

}
