package org.majimena.petz.security.audit;

import org.junit.Test;
import org.majimena.petz.datatype.LangKey;
import org.majimena.petz.datatype.TimeZone;
import org.majimena.petz.security.PetzUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see SpringSecurityAuditorAware
 */
public class SpringSecurityAuditorAwareTest {

    private SpringSecurityAuditorAware sut = new SpringSecurityAuditorAware();

    @Test
    public void ログイン中のユーザIDが取得できること() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", LangKey.ENGLISH, TimeZone.ASIA_TOKYO, Arrays.asList()), "anonymous"));
        SecurityContextHolder.setContext(context);
        assertThat(sut.getCurrentAuditor(), is("123"));
    }
}