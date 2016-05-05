package org.majimena.petical.client.recaptcha;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by todoken on 2016/05/05.
 */
public class RecaptchaRestAdapterFactory {
    public static RestAdapter create() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return new RestAdapter.Builder()
                .setEndpoint("https://www.google.com/recaptcha/api")
                .setConverter(new GsonConverter(gson))
                .build();
    }
}
