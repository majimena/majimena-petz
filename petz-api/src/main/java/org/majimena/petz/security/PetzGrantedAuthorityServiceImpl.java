package org.majimena.petz.security;

import org.majimena.petz.domain.authentication.PetzGrantedAuthority;
import org.majimena.petz.repository.ClinicStaffRepository;
import org.majimena.petz.security.GrantedAuthorityService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * クリニック権限サービスの実装クラス.
 */
@Component
@Transactional(readOnly = true)
public class PetzGrantedAuthorityServiceImpl implements GrantedAuthorityService {

    /**
     * クリニックスタッフリポジトリ.
     */
    @Inject
    private ClinicStaffRepository clinicStaffRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PetzGrantedAuthority> getAuthoritiesByUserId(String userId) {
        return clinicStaffRepository.findByUserId(userId)
                .stream()
                .map(staff -> new PetzGrantedAuthority(staff.getClinic().getId(), staff.getRole()))
                .collect(Collectors.toList());
    }
}
