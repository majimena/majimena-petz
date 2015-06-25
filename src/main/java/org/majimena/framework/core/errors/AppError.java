package org.majimena.framework.core.errors;

import lombok.*;

import java.io.Serializable;

/**
 * Created by todoken on 2015/06/25.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AppError implements Serializable {

    // FIXME メッセージリソース対応したほうがいいね
    private static final long serialVersionUID = 7544581787985166085L;
    private String code;
    private String message;
    private String reference;

}
