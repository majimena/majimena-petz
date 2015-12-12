package org.majimena.petz.service;

import org.majimena.petz.domain.ClinicInvitation;

import java.util.Set;

/**
 * クリニックスタッフサービス.
 */
public interface ClinicInvitationService {

    ClinicInvitation findClinicInvitationById(String invitationId);

    void inviteStaff(String clinicId, Set<String> emails);

    void activate(String invitationId, String activationKey);

}
