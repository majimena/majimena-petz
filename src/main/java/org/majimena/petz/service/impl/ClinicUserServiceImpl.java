package org.majimena.petz.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.common.exceptions.ApplicationException;
import org.majimena.petz.domain.Clinic;
import org.majimena.petz.domain.ClinicUser;
import org.majimena.petz.domain.User;
import org.majimena.petz.domain.UserContact;
import org.majimena.petz.domain.clinic.ClinicUserAuthorizationToken;
import org.majimena.petz.domain.clinic.ClinicUserCriteria;
import org.majimena.petz.domain.errors.ErrorCode;
import org.majimena.petz.repository.ClinicRepository;
import org.majimena.petz.repository.ClinicUserRepository;
import org.majimena.petz.repository.UserContactRepository;
import org.majimena.petz.repository.UserRepository;
import org.majimena.petz.service.ClinicUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * クリニックユーザーサービスの実装.
 */
@Service
@Transactional
public class ClinicUserServiceImpl implements ClinicUserService {

    @Inject
    private ClinicRepository clinicRepository;

    @Inject
    private ClinicUserRepository clinicUserRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserContactRepository userContactRepository;

    @Override
    public Page<ClinicUser> getUsersByClinicUserCriteria(ClinicUserCriteria criteria, Pageable pageable) {
        return clinicUserRepository.findAll(ClinicUserRepository.Spec.of(criteria), pageable);
    }

    @Override
    public void authorize(ClinicUserAuthorizationToken token) throws ApplicationException {
        Clinic clinic = clinicRepository.findOne(token.getClinicId());
        if (clinic == null) {
            throw new ApplicationException(ErrorCode.PTZ_001999);
        }
        User user = userRepository.findOne(token.getUserId());
        if (user == null) {
            throw new ApplicationException(ErrorCode.PTZ_000999);
        }
        UserContact contact = userContactRepository.findOne(token.getUserId());
        if (contact == null) {
            throw new ApplicationException(ErrorCode.PTZ_000999);
        }

        // 登録情報と合っていればクリニックのユーザーにする
        if (StringUtils.equals(user.getFirstName(), token.getFirstName())
            && StringUtils.equals(user.getLastName(), token.getLastName())
            && StringUtils.equals(contact.getPhoneNo(), token.getPhoneNo())) {
            clinicUserRepository.findByClinicIdAndUserId(token.getClinicId(), token.getUserId())
                .map(u -> {
                    u.setClinic(clinic);
                    u.setUser(user);
                    return clinicUserRepository.save(u);
                })
                .orElseGet(() -> clinicUserRepository.save(new ClinicUser(null, clinic, user)));
        } else {
            // 認証エラー
            throw new ApplicationException(ErrorCode.PTZ_000201);
        }
    }
}
