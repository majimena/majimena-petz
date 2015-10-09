package org.majimena.petz.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;
import org.majimena.petz.domain.Blood;
import org.majimena.petz.domain.BloodDeserializer;
import org.majimena.petz.domain.common.SexType;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @see BloodDeserializer
 */
public class BloodDeserializerTest {

    private BloodDeserializer sut = new BloodDeserializer();

    @Mocked
    private JsonParser jp;

    @Mocked
    private DeserializationContext context;

    @Test
    public void 血液型ドメインにデシリアライズできること() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_STRING;
            jp.getText();
            result = "DEA1.1";
        }};

        Blood result = sut.deserialize(jp, context);
        assertThat(result, is(new Blood("DEA1.1")));
    }

    @Test
    public void 値が入っていない場合はnulになること() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_STRING;
            jp.getText();
            result = "";
        }};

        Blood result = sut.deserialize(jp, context);
        assertThat(result, is(nullValue()));
    }

    @Test(expected = JsonMappingException.class)
    public void 文字列以外の場合は変換できないこと() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_NUMBER_INT;
            context.mappingException(SexType.class);
            result = new JsonMappingException("test");
        }};

        sut.deserialize(jp, context);
    }
}
