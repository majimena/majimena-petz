package org.majimena.petical.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see BloodSerializer
 */
public class BloodSerializerTest {

    private BloodSerializer sut = new BloodSerializer();

    @Mocked
    private JsonGenerator generator;

    @Mocked
    private SerializerProvider provider;

    @Test
    public void 文字列にシリアライズできること() throws Exception {
        sut.serialize(new Blood("DEA1.1"), generator, provider);

        new Verifications() {{
            String value;
            generator.writeString(value = withCapture());

            assertThat(value, is("DEA1.1"));
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
