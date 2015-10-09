package org.majimena.petz.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Sets;
import mockit.FullVerificationsInOrder;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;
import org.majimena.petz.domain.Tag;
import org.majimena.petz.domain.TagSetSerializer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @see TagSetSerializer
 */
public class TagSetSerializerTest {

    private TagSetSerializer sut = new TagSetSerializer();

    @Mocked
    private JsonGenerator generator;

    @Mocked
    private SerializerProvider provider;

    @Test
    public void 文字列にシリアライズできること() throws Exception {
        sut.serialize(Sets.newHashSet(new Tag("tag1"), null, new Tag("tag2")), generator, provider);

        new FullVerificationsInOrder() {{
            String value1;
            String value2;
            generator.writeStartArray();
            generator.writeString(value1 = withCapture());
            generator.writeString(value2 = withCapture());
            generator.writeEndArray();

            assertThat(value1, is("tag1"));
            assertThat(value2, is("tag2"));
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
