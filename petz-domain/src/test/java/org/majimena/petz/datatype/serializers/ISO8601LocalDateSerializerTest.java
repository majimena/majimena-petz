package org.majimena.petz.datatype.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Test;
import org.majimena.petz.datatype.serializers.ISO8601LocalDateSerializer;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by todoken on 2015/07/29.
 */
public class ISO8601LocalDateSerializerTest {

    private ISO8601LocalDateSerializer sut = new ISO8601LocalDateSerializer();

    @Mocked
    private JsonGenerator generator;

    @Mocked
    private SerializerProvider provider;

    @Test
    public void 文字列にシリアライズできること() throws Exception {
        new NonStrictExpectations() {{
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
}
