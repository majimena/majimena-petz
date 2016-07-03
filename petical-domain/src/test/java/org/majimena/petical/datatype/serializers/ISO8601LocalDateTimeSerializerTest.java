package org.majimena.petical.datatype.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Test;
import org.majimena.petical.security.authentication.PetzUser;
import org.majimena.petical.security.authentication.PetzUserKey;
import org.majimena.petical.datatype.LangKey;
import org.majimena.petical.datatype.TimeZone;
import org.majimena.petical.security.SecurityUtils;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see ISO8601LocalDateTimeSerializer
 */
public class ISO8601LocalDateTimeSerializerTest {

    private ISO8601LocalDateTimeSerializer sut = new ISO8601LocalDateTimeSerializer();

    @Mocked
    private SecurityUtils securityUtils;

    @Mocked
    private JsonGenerator generator;

    @Mocked
    private SerializerProvider provider;

    @Test
    public void タイムゾーンが指定されている時に文字列にシリアライズできること() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PetzUserKey.LANG, LangKey.JAPANESE);
        properties.put(PetzUserKey.TIMEZONE, TimeZone.ASIA_TOKYO);

        new NonStrictExpectations() {{
            SecurityUtils.getPrincipal();
            result = Optional.of(new PetzUser("userId", "username", "password", properties, Collections.<GrantedAuthority>emptyList()));
            generator.writeString(anyString);
        }};

        LocalDateTime value = LocalDateTime.of(2015, 4, 5, 15, 0, 0);
        sut.serialize(value, generator, provider);

        new Verifications() {{
            String value;
            generator.writeString(value = withCapture());
            assertThat(value, is("2015-04-06T00:00:00+09:00"));
        }};
    }

    @Test
    public void タイムゾーンが指定されていない時はUTCの文字列にシリアライズできること() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PetzUserKey.LANG, LangKey.JAPANESE);

        new NonStrictExpectations() {{
            SecurityUtils.getPrincipal();
            result = Optional.of(new PetzUser("userId", "username", "password", properties, Collections.<GrantedAuthority>emptyList()));
            generator.writeString(anyString);
        }};

        LocalDateTime value = LocalDateTime.of(2015, 4, 5, 15, 0, 0);
        sut.serialize(value, generator, provider);

        new Verifications() {{
            String value;
            generator.writeString(value = withCapture());
            assertThat(value, is("2015-04-05T15:00:00Z"));
        }};
    }

    @Test
    public void ログインユーザがいない時はUTCの文字列にシリアライズできること() throws Exception {
        new NonStrictExpectations() {{
            SecurityUtils.getPrincipal();
            result = Optional.ofNullable(null);
            generator.writeString(anyString);
        }};

        LocalDateTime value = LocalDateTime.of(2015, 4, 5, 15, 0, 0);
        sut.serialize(value, generator, provider);

        new Verifications() {{
            String value;
            generator.writeString(value = withCapture());
            assertThat(value, is("2015-04-05T15:00:00Z"));
        }};
    }
}
