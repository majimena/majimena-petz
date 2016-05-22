package org.majimena.petical.security;

import org.majimena.petical.authentication.PetzGrantedAuthority;

import java.util.List;

/**
 * クリニック権限サービス.
 */
public interface GrantedAuthorityService {

    /**
     * ユーザーIDをもとに権限を取得する.
     *
     * @param userId ユーザーID
     * @return 権限
     */
    List<PetzGrantedAuthority> getAuthoritiesByUserId(String userId);
}
