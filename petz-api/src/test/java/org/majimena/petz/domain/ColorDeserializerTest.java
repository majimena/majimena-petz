package org.majimena.petz.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;
import org.majimena.petz.domain.Color;
import org.majimena.petz.domain.ColorDeserializer;
import org.majimena.petz.domain.common.SexType;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @see ColorDeserializer
 */
public class ColorDeserializerTest {

    private ColorDeserializer sut = new ColorDeserializer();

    @Mocked
    private JsonParser jp;

    @Mocked
    private DeserializationContext context;

    @Test
    public void 毛色ドメインにデシリアライズできること() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_STRING;
            jp.getText();
            result = "white";
        }};

        Color result = sut.deserialize(jp, context);
        assertThat(result, is(new Color("white")));
    }

    @Test
    public void 値が入っていない場合はnulになること() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_STRING;
            jp.getText();
            result = "";
        }};

        Color result = sut.deserialize(jp, context);
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
