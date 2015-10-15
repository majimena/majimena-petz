package org.majimena.petz.security;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.datatype.TimeZone;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;

import java.util.Collection;
import java.util.Optional;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<PetzUser> getPrincipal() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof PetzUser) {
                return Optional.of((PetzUser) authentication.getPrincipal());
            }
        }
        return Optional.empty();
    }

    public static String getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails springSecurityUser = null;
        String userName = null;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }
        return userName;
    }

    public static String getCurrentUserId() {
        Optional<PetzUser> principal = getPrincipal();
        return principal.map(p -> p.getUserId()).orElse(null);
    }

    /**
     * 現在のユーザが設定しているタイムゾーンを取得する.
     *
     * @return タイムゾーン
     */
    public static TimeZone getCurrentTimeZone() {
        TimeZone timeZone = getPrincipal().map(p -> p.getTimeZone()).orElse(TimeZone.UTC);
        if (timeZone == null) {
            timeZone = TimeZone.UTC;
        }
        return timeZone;
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 認証ユーザーがロールを持っているかチェックする.
     *
     * @param role ロール
     * @return 認証ユーザーがロールを持っていればTRUE
     */
    public static boolean isUserInRole(String role) {
        return isUserInRole(null, role);
    }

    /**
     * 認証ユーザーがクリニック限定のロールを持っているかチェックする.
     *
     * @param clinicId クリニックID
     * @param role     ロール
     * @return 認証ユーザーがロールを持っていればTRUE
     */
    public static boolean isUserInRole(String clinicId, String role) {
        return getPrincipal()
                .map(u -> u.getAuthorities().contains(new PetzGrantedAuthority(clinicId, role)))
                .orElse(false);
    }

    /**
     * 認証ユーザーが何かしらのクリニック限定のロールを持っているかチェックする.
     *
     * @param clinicId クリニックID
     * @return 認証ユーザーがロールを持っていればTRUE
     */
    public static boolean isUserInClinic(String clinicId) {
        return getPrincipal()
                .map(u -> u.getAuthorities().stream()
                        .anyMatch(ga -> StringUtils.endsWith(ga.getAuthority(), clinicId)))
                .orElse(false);
    }

    /**
     * 認証済みユーザーが指定したクリニックのロールを持っていない場合に例外を投げる.
     *
     * @param clinicId クリニックID
     * @throws UnauthorizedUserException クリニックのロールを持っていない場合
     */
    public static void throwIfDoNotHaveClinicRoles(String clinicId) {
        if (isUserInClinic(clinicId)) {
            return;
        }
        throw new UnauthorizedUserException("Cannot access resource.");
    }
}
