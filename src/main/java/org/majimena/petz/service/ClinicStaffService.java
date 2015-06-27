package org.majimena.petz.service;

import java.util.Set;

/**
 * クリニックスタッフサービス.
 */
public interface ClinicStaffService {

    void inviteStaff(Long clinicId, Set<String> emails);

    void activate(String activationKey);

}
