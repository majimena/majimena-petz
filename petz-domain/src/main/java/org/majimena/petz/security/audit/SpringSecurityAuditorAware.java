package org.majimena.petz.security.audit;

import org.majimena.petz.security.SecurityUtils;
import org.springframework.data.domain.AuditorAware;

import javax.inject.Named;

/**
 * Spring Security上の認証ユーザを取得するAuditorAware.
 */
@Named("springSecurityAuditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentAuditor() {
        return SecurityUtils.getCurrentUserId();
    }
}
