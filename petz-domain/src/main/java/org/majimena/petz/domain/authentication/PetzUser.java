package org.majimena.petz.domain.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
     * プロパティ.
     */
    private Map<String, Object> properties = new HashMap<>();

    /**
     * コンストラクタ.
     *
     * @param userId      ユーザーID
     * @param username    ログインID
     * @param password    パスワード
     * @param properties  プロパティ
     * @param authorities 権限
     */
    public PetzUser(String userId, String username, String password, Map<String, Object> properties, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
        this.properties = properties;
    }

    /**
     * ユーザーIDを取得する.
     *
     * @return ユーザーID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * プロパティを取得する.
     *
     * @param key   キー
     * @param clazz 戻り値のクラス
     * @param <T>   戻り値の型
     * @return 値
     */
    public <T> T get(String key, Class<T> clazz) {
        Object o = properties.get(key);
        if (o != null) {
            return clazz.cast(o);
        }
        return null;
    }

    /**
     * プロパティを保存する.
     *
     * @param key   キー
     * @param value 値
     */
    public void put(String key, Object value) {
        properties.put(key, value);
    }
}
