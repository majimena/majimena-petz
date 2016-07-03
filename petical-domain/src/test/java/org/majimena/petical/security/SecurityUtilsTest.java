package org.majimena.petical.security;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.After;
import org.junit.Test;
import org.majimena.petical.security.authentication.PetzGrantedAuthority;
import org.majimena.petical.security.authentication.PetzUser;
import org.majimena.petical.security.authentication.PetzUserKey;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.datatype.TimeZone;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see SecurityUtils
 */
public class SecurityUtilsTest {

    @Mocked
    private GrantedAuthorityService grantedAuthorityService;

    private static Map<String, Object> createProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PetzUserKey.LANG, LangKey.ENGLISH);
        properties.put(PetzUserKey.TIMEZONE, TimeZone.ASIA_TOKYO);
        return properties;
    }

    @After
    public void tearDown() {
        // 毎回セキュリティコンテキストを初期化する
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        String login = SecurityUtils.getCurrentLogin();
        assertThat(login, is("admin"));
    }

    @Test
    public void testIsAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        assertThat(isAuthenticated, is(true));
    }

    @Test
    public void testAnonymousIsNotAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new PetzGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        assertThat(isAuthenticated, is(false));
    }

    @Test
    public void ログインしていない時にシステムのユーザIDが取得できること() {
        assertThat(SecurityUtils.getCurrentUserId(), is(SecurityUtils.SYSTEM_ACCOUNT));
    }

    @Test
    public void ログインしている時にログイン中のユーザIDが取得できること() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", createProperties(), Arrays.asList()), "anonymous"));
        SecurityContextHolder.setContext(context);
        assertThat(SecurityUtils.getCurrentUserId(), is("123"));
    }

    @Test
    public void ログインしていない時にデフォルトのタイムゾーンが取得できること() {
        assertThat(SecurityUtils.getCurrentTimeZone(), is(TimeZone.UTC));
    }

    @Test
    public void ログインしている時にユーザ設定のタイムゾーンが取得できること() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", createProperties(), Arrays.asList()), "anonymous"));
        SecurityContextHolder.setContext(context);
        assertThat(SecurityUtils.getCurrentTimeZone(), is(TimeZone.ASIA_TOKYO));
    }

    @Test
    public void ログインしている時にデフォルトのタイムゾーンが取得できること() {
        Map<String, Object> properties = createProperties();
        properties.put(PetzUserKey.TIMEZONE, null);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", properties, Arrays.asList()), "anonymous"));
        SecurityContextHolder.setContext(context);
        assertThat(SecurityUtils.getCurrentTimeZone(), is(TimeZone.UTC));
    }

    @Test
    public void ユーザーの役割権限チェックができること() {
        Map<String, Object> properties = createProperties();
        properties.put(PetzUserKey.TIMEZONE, TimeZone.UTC);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new PetzGrantedAuthority("ROLE_USER"));
        authorities.add(new PetzGrantedAuthority("1", "ROLE_CLINIC_ADMIN"));
        authorities.add(new PetzGrantedAuthority("2", "ROLE_CLINIC_ADMIN"));
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", properties, authorities), "anonymous"));
        SecurityContextHolder.setContext(context);

        assertThat(SecurityUtils.isUserInRole("ROLE_USER"), is(true));
        assertThat(SecurityUtils.isUserInRole("ROLE_CLINIC_ADMIN"), is(false));
    }

    @Test
    public void クリニックの役割権限チェックができること() {
        Map<String, Object> properties = createProperties();
        properties.put(PetzUserKey.TIMEZONE, TimeZone.UTC);

        List<PetzGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new PetzGrantedAuthority("1", "ROLE_CLINIC_ADMIN"));
        authorities.add(new PetzGrantedAuthority("2", "ROLE_CLINIC_ADMIN"));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", properties, authorities), "anonymous"));
        SecurityContextHolder.setContext(context);

        SecurityUtils.setGrantedAuthorityService(grantedAuthorityService);
        new NonStrictExpectations() {{
            grantedAuthorityService.getAuthoritiesByUserId("123");
            result = authorities;
        }};

        assertThat(SecurityUtils.isUserInRole("1", "ROLE_CLINIC_ADMIN"), is(true));
        assertThat(SecurityUtils.isUserInRole("2", "ROLE_CLINIC_ADMIN"), is(true));
        assertThat(SecurityUtils.isUserInRole("3", "ROLE_CLINIC_ADMIN"), is(false));
        assertThat(SecurityUtils.isUserInRole("1", "ROLE_USER"), is(false));
    }

    @Test
    public void クリニックの権限チェックができること() {
        Map<String, Object> properties = createProperties();
        properties.put(PetzUserKey.TIMEZONE, TimeZone.UTC);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new PetzGrantedAuthority("ROLE_USER"));
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", properties, authorities), "anonymous"));
        SecurityContextHolder.setContext(context);

        SecurityUtils.setGrantedAuthorityService(grantedAuthorityService);
        new NonStrictExpectations() {{
            List<PetzGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new PetzGrantedAuthority("1", "ROLE_CLINIC_ADMIN"));
            authorities.add(new PetzGrantedAuthority("d8272af2-75cc-47b3-97e9-8ba631a569f0", "ROLE_CLINIC_ADMIN"));
            grantedAuthorityService.getAuthoritiesByUserId("123");
            result = authorities;
        }};

        assertThat(SecurityUtils.isUserInClinic("1"), is(true));
        assertThat(SecurityUtils.isUserInClinic("d8272af2-75cc-47b3-97e9-8ba631a569f0"), is(true));
        assertThat(SecurityUtils.isUserInClinic("3"), is(false));
    }
}
