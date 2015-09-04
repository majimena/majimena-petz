package org.majimena.petz.service;

import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.domain.ClinicUser;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.clinic.ClinicUserAuthorizationToken;
import org.majimena.petz.domain.clinic.ClinicUserCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * クリニックユーザーサービス.
 */
public interface ClinicUserService {

    Page<ClinicUser> getUsersByClinicUserCriteria(ClinicUserCriteria criteria, Pageable pageable);

    void authorize(ClinicUserAuthorizationToken token) throws ApplicationException;

}
