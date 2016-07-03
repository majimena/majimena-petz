package org.majimena.petical.security.audit;

import org.junit.Test;
import org.majimena.petical.security.authentication.PetzUser;
import org.majimena.petical.security.authentication.PetzUserKey;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.datatype.TimeZone;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see SpringSecurityAuditorAware
 */
public class SpringSecurityAuditorAwareTest {

    private SpringSecurityAuditorAware sut = new SpringSecurityAuditorAware();

    private static Map<String, Object> createProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PetzUserKey.LANG, LangKey.ENGLISH);
        properties.put(PetzUserKey.TIMEZONE, TimeZone.ASIA_TOKYO);
        return properties;
    }

    @Test
    public void ログイン中のユーザIDが取得できること() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", createProperties(), Arrays.asList()), "anonymous"));
        SecurityContextHolder.setContext(context);
        assertThat(sut.getCurrentAuditor(), is("123"));
    }
}