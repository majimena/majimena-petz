package org.majimena.framework.core.datatypes.converters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by todoken on 2015/07/27.
 */
public class ISO8601LocalDateDeserializerTest {

    private ISO8601LocalDateDeserializer sut = new ISO8601LocalDateDeserializer();

    @Mocked
    private JsonParser jp;

    @Mocked
    private DeserializationContext context;

    @Test
    public void 日付にデシリアライズできること() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.VALUE_STRING;
            jp.getText();
            result = "2015-09-06T00:00:00+09:00";
        }};

        LocalDate result = sut.deserialize(jp, context);
        assertThat(result.toString(), is("2015-09-06"));
    }

    @Test(expected = JsonMappingException.class)
    public void 文字列以外の場合は変換できないこと() throws Exception {
        new NonStrictExpectations() {{
            jp.getCurrentToken();
            result = JsonToken.START_OBJECT;
            context.mappingException(LocalDate.class);
            result = new JsonMappingException("test");
        }};

        sut.deserialize(jp, context);
    }
}
