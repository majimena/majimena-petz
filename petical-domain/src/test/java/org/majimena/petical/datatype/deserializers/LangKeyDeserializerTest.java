package org.majimena.petical.datatype.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;
import org.majimena.petical.datatype.LangKey;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @see LangKeyDeserializer
 */
public class LangKeyDeserializerTest {

    private LangKeyDeserializer sut = new LangKeyDeserializer();

    @Mocked
    private JsonParser jp;

    @Mocked
    private DeserializationContext context;

    @Test
    public void 言語キー型にデシリアライズできること() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_STRING;
            jp.getText();
            result = "ENGLISH";
        }};

        LangKey result = sut.deserialize(jp, context);
        assertThat(result, is(LangKey.ENGLISH));
    }

    @Test
    public void 値が入っていない場合はNULLになること() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_STRING;
            jp.getText();
            result = "";
        }};

        LangKey result = sut.deserialize(jp, context);
        assertThat(result, is(nullValue()));
    }

    @Test(expected = JsonMappingException.class)
    public void 文字列以外の場合は変換できないこと() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_NUMBER_INT;
            context.mappingException(LangKey.class);
            result = new JsonMappingException("test");
        }};

        sut.deserialize(jp, context);
    }
}
