package org.majimena.petz.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by todoken on 2015/07/06.
 */
public class PetzUser extends User {

    private String userId;

    public PetzUser(String userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
