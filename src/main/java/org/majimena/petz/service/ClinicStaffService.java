package org.majimena.petz.service;

import java.util.Set;

/**
 * クリニックスタッフサービス.
 */
public interface ClinicStaffService {

    void inviteStaff(String clinicId, Set<String> emails);

    void activate(String activationKey);

}
