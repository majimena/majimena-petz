package org.majimena.petz.datatype.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Test;
import org.majimena.petz.datatype.LangKey;
import org.majimena.petz.datatype.TimeZone;
import org.majimena.petz.domain.authentication.PetzUser;
import org.majimena.petz.domain.authentication.PetzUserKey;
import org.majimena.petz.security.SecurityUtils;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see ISO8601LocalDateSerializer
 */
public class ISO8601LocalDateSerializerTest {

    private ISO8601LocalDateSerializer sut = new ISO8601LocalDateSerializer();

    @Mocked
    private SecurityUtils securityUtils;

    @Mocked
    private JsonGenerator generator;

    @Mocked
    private SerializerProvider provider;

    @Test
    public void 指定のタイムゾーンで文字列にシリアライズできること() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PetzUserKey.LANG, LangKey.JAPANESE);
        properties.put(PetzUserKey.TIMEZONE, TimeZone.ASIA_TOKYO);

        new NonStrictExpectations() {{
            SecurityUtils.getPrincipal();
            result = Optional.of(new PetzUser("userId", "username", "password", properties, Collections.<GrantedAuthority>emptyList()));
            generator.writeString(anyString);
        }};

        LocalDate value = LocalDate.of(2015, 4, 5);
        sut.serialize(value, generator, provider);

        new Verifications() {{
            String value;
            generator.writeString(value = withCapture());
            assertThat(value, is("2015-04-05T00:00:00+09:00"));
        }};
    }

    @Test
    public void タイムゾーンが指定されていない場合はUTCで文字列にシリアライズできること() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PetzUserKey.LANG, LangKey.JAPANESE);

        new NonStrictExpectations() {{
            SecurityUtils.getPrincipal();
            result = Optional.of(new PetzUser("userId", "username", "password", properties, Collections.<GrantedAuthority>emptyList()));
            generator.writeString(anyString);
        }};

        LocalDate value = LocalDate.of(2015, 4, 5);
        sut.serialize(value, generator, provider);

        new Verifications() {{
            String value;
            generator.writeString(value = withCapture());
            assertThat(value, is("2015-04-05T00:00:00Z"));
        }};
    }

    @Test
    public void ログインユーザがいない場合はUTCで文字列にシリアライズできること() throws Exception {
        new NonStrictExpectations() {{
            SecurityUtils.getPrincipal();
            result = Optional.ofNullable(null);
            generator.writeString(anyString);
        }};

        LocalDate value = LocalDate.of(2015, 4, 5);
        sut.serialize(value, generator, provider);

        new Verifications() {{
            String value;
            generator.writeString(value = withCapture());
            assertThat(value, is("2015-04-05T00:00:00Z"));
        }};
    }
}
