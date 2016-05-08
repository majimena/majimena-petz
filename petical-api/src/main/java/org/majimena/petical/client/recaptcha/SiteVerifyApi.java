package org.majimena.petical.client.recaptcha;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * <i>https://www.google.com/recaptcha/api/siteverify</i>のAPIインタフェース.
 */
public interface SiteVerifyApi {
    @POST("/siteverify")
    SiteVerifyEntity post(@Query("secret") String secret, @Query("response") String response, @Body String body);
}
