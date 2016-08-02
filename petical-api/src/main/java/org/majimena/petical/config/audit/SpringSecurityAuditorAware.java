package org.majimena.petical.config.audit;

import org.majimena.petical.security.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.inject.Named;

/**
 * Spring Security上の認証ユーザを取得するAuditorAware.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentAuditor() {
        return SecurityUtils.getCurrentUserId();
    }
}
