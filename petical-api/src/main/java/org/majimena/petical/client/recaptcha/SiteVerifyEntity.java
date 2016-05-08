package org.majimena.petical.client.recaptcha;

import lombok.Builder;
import lombok.Data;

/**
 * <i>https://www.google.com/recaptcha/api/siteverify</i>のレスポンスエンティティ.
 */
@Data
@Builder
public class SiteVerifyEntity {
    private boolean success;
    private String challengeTs;
    private String hostname;
}
