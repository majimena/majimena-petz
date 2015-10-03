package org.majimena.petz.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @see TagSetDeserializer
 */
public class TagSetDeserializerTest {

    private TagSetDeserializer sut = new TagSetDeserializer();

    @Mocked
    private JsonParser jp;

    @Mocked
    private DeserializationContext context;

    @Test
    public void タグドメインにデシリアライズできること() throws Exception {
        new StrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.START_ARRAY;

            jp.nextToken();
            result = JsonToken.VALUE_STRING;
            jp.getValueAsString();
            result = "tag1";
            jp.nextToken();
            result = JsonToken.VALUE_STRING;
            jp.getValueAsString();
            result = "";
            jp.nextToken();
            result = JsonToken.VALUE_STRING;
            jp.getValueAsString();
            result = "tag2";

            jp.nextToken();
            result = JsonToken.END_ARRAY;
        }};

        Set<Tag> result = sut.deserialize(jp, context);
        assertThat(result.size(), is(2));
        assertThat(result.contains(new Tag("tag1")), is(true));
        assertThat(result.contains(new Tag("tag2")), is(true));
    }

    @Test(expected = JsonMappingException.class)
    public void 文字列以外の場合は変換できないこと() throws Exception {
        new StrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_NUMBER_INT;
        }};

        sut.deserialize(jp, context);
    }
}
