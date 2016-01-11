package org.majimena.petz.domain.authentication;

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
     * クリニックIDを取得する.
     *
     * @return クリニックID
     */
    public String getClinicId() {
        return clinicId;
    }

    /**
     * 権限を取得する.
     *
     * @return 権限
     */
    public String getRole() {
        return role;
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
