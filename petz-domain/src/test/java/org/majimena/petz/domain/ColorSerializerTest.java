package org.majimena.petz.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Test;
import org.majimena.petz.domain.Color;
import org.majimena.petz.domain.ColorSerializer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see ColorSerializer
 */
public class ColorSerializerTest {

    private ColorSerializer sut = new ColorSerializer();

    @Mocked
    private JsonGenerator generator;

    @Mocked
    private SerializerProvider provider;

    @Test
    public void 文字列にシリアライズできること() throws Exception {
        sut.serialize(new Color("white"), generator, provider);

        new Verifications() {{
            String value;
            generator.writeString(value = withCapture());

            assertThat(value, is("white"));
        }};
    }

    @Test
    public void 値がない場合は何もされないこと() throws Exception {
        new NonStrictExpectations() {{
            generator.writeString(anyString);
            times = 0;
        }};

        sut.serialize(null, generator, provider);
    }
}
