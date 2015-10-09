package org.majimena.petz.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 認証ユーザー情報.
 */
public class PetzUser extends User {

    /**
     * シリアルバージョンID.
     */
    private static final long serialVersionUID = -2303637259648645016L;

    /**
     * ユーザーID.
     */
    private String userId;

    /**
     * コンストラクタ.
     *
     * @param userId      ユーザーID
     * @param username    ログインID
     * @param password    パスワード
     * @param authorities 権限
     */
    public PetzUser(String userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    /**
     * ユーザーIDを取得する.
     *
     * @return ユーザーID
     */
    public String getUserId() {
        return userId;
    }
}
