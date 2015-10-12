package org.majimena.petz.security;

import org.majimena.petz.datatype.LangKey;
import org.majimena.petz.datatype.TimeZone;
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
     * 言語.
     */
    private LangKey langKey;

    /**
     * タイムゾーン.
     */
    private TimeZone timeZone;

    /**
     * コンストラクタ.
     *
     * @param userId      ユーザーID
     * @param username    ログインID
     * @param password    パスワード
     * @param langKey     言語
     * @param timeZone    タイムゾーン
     * @param authorities 権限
     */
    public PetzUser(String userId, String username, String password, LangKey langKey, TimeZone timeZone, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
        this.langKey = langKey;
        this.timeZone = timeZone;
    }

    /**
     * ユーザーIDを取得する.
     *
     * @return ユーザーID
     */
    public String getUserId() {
        return userId;
    }

    public LangKey getLangKey() {
        return langKey;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }
}
