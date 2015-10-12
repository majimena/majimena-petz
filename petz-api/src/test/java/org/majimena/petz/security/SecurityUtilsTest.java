package org.majimena.petz.security;

import org.junit.Test;
import org.majimena.petz.datatype.LangKey;
import org.majimena.petz.datatype.TimeZone;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test class for the SecurityUtils utility class.
 *
 * @see SecurityUtils
 */
public class SecurityUtilsTest {

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
    public void ユーザーの役割権限チェックができること() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new PetzGrantedAuthority("ROLE_USER"));
        authorities.add(new PetzGrantedAuthority("1", "ROLE_CLINIC_ADMIN"));
        authorities.add(new PetzGrantedAuthority("2", "ROLE_CLINIC_ADMIN"));
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", LangKey.ENGLISH, TimeZone.UTC, authorities), "anonymous"));
        SecurityContextHolder.setContext(context);

        assertThat(SecurityUtils.isUserInRole("ROLE_USER"), is(true));
        assertThat(SecurityUtils.isUserInRole("ROLE_CLINIC_ADMIN"), is(false));
    }

    @Test
    public void クリニックの役割権限チェックができること() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new PetzGrantedAuthority("ROLE_USER"));
        authorities.add(new PetzGrantedAuthority("1", "ROLE_CLINIC_ADMIN"));
        authorities.add(new PetzGrantedAuthority("2", "ROLE_CLINIC_ADMIN"));
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", LangKey.ENGLISH, TimeZone.UTC, authorities), "anonymous"));
        SecurityContextHolder.setContext(context);

        assertThat(SecurityUtils.isUserInRole("1", "ROLE_CLINIC_ADMIN"), is(true));
        assertThat(SecurityUtils.isUserInRole("2", "ROLE_CLINIC_ADMIN"), is(true));
        assertThat(SecurityUtils.isUserInRole("3", "ROLE_CLINIC_ADMIN"), is(false));
        assertThat(SecurityUtils.isUserInRole("1", "ROLE_USER"), is(false));
    }

    @Test
    public void クリニックの権限チェックができること() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new PetzGrantedAuthority("ROLE_USER"));
        authorities.add(new PetzGrantedAuthority("1", "ROLE_CLINIC_ADMIN"));
        authorities.add(new PetzGrantedAuthority("d8272af2-75cc-47b3-97e9-8ba631a569f0", "ROLE_CLINIC_ADMIN"));
        context.setAuthentication(new UsernamePasswordAuthenticationToken(new PetzUser("123", "anonymous", "anonymous", LangKey.ENGLISH, TimeZone.UTC, authorities), "anonymous"));
        SecurityContextHolder.setContext(context);

        assertThat(SecurityUtils.isUserInClinic("1"), is(true));
        assertThat(SecurityUtils.isUserInClinic("d8272af2-75cc-47b3-97e9-8ba631a569f0"), is(true));
        assertThat(SecurityUtils.isUserInClinic("3"), is(false));
    }
}
