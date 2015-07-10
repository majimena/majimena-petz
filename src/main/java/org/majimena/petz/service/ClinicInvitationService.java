package org.majimena.petz.service;

import org.majimena.petz.domain.ClinicInvitation;

import java.util.Optional;
import java.util.Set;

/**
 * クリニックスタッフサービス.
 */
public interface ClinicInvitationService {

    Optional<ClinicInvitation> findClinicInvitationById(String invitationId);

    void inviteStaff(String clinicId, Set<String> emails);

    void activate(String activationKey);

}
