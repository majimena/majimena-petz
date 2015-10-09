package org.majimena.petz.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

/**
 * 権限.
 */
public class PetzGrantedAuthority implements GrantedAuthority {

    /**
     * シリアルバージョンID.
     */
    private static final long serialVersionUID = -418644590399759605L;

    /**
     * クリニックID.
     */
    private String clinicId;

    /**
     * 権限.
     */
    private String role;

    /**
     * コンストラクタ.
     *
     * @param role 役割
     */
    public PetzGrantedAuthority(String role) {
        this.role = role;
    }

    /**
     * コンストラクタ.
     *
     * @param clinicId クリニックID
     * @param role     役割
     */
    public PetzGrantedAuthority(String clinicId, String role) {
        this.clinicId = clinicId;
        this.role = role;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthority() {
        if (StringUtils.isEmpty(clinicId)) {
            return role;
        } else {
            return role + "-" + clinicId;
        }
    }
}
