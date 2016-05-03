package org.majimena.petical.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ユーザーアウトライン.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOutline implements Serializable {
    /**
     * ID.
     */
    private String id;

    /**
     * 氏名（姓）.
     */
    @Deprecated
    private String firstName;

    /**
     * 氏名（名）.
     */
    @Deprecated
    private String lastName;

    /**
     * ユーザー名.
     */
    private String username;

    /**
     * アクティベートされているか.
     */
    private Boolean activated;
}
