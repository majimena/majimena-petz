package org.majimena.petz.datatype.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see ISO8601LocalDateTimeSerializer
 */
public class ISO8601LocalDateTimeSerializerTest {

    private ISO8601LocalDateTimeSerializer sut = new ISO8601LocalDateTimeSerializer();

    @Mocked
    private JsonGenerator generator;

    @Mocked
    private SerializerProvider provider;

    @Test
    public void 文字列にシリアライズできること() throws Exception {
        new NonStrictExpectations() {{
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
}
