package org.majimena.framework.core.datatypes.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Test;
import org.majimena.framework.core.datatypes.EnumDataType;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by todoken on 2015/08/10.
 */
public class EnumDataTypeSerializerTest {

    private EnumDataTypeSerializer sut = new EnumDataTypeSerializer();

    @Mocked
    private JsonGenerator generator;

    @Mocked
    private SerializerProvider provider;

    @Test
    public void 文字列にシリアライズできること() throws Exception {
        new NonStrictExpectations() {{
            generator.writeString(anyString);
        }};

        sut.serialize(Hoge.FOO, generator, provider);

        new Verifications() {{
            String value;
            generator.writeString(value = withCapture());

            assertThat(value, is(Hoge.FOO.name()));
        }};
    }

    public enum Hoge implements EnumDataType {
        FOO("ふー"), BAR("ばー");

        private String name;

        Hoge(String name) {
            this.name = name();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return name();
        }
    }
}
