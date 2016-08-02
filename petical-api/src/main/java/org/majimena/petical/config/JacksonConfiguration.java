package org.majimena.petical.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.majimena.petical.common.factory.JacksonJsonFactory;
import org.majimena.petical.common.factory.JsonFactory;
import org.majimena.petical.config.jackson.BooleanDeserializer;
import org.majimena.petical.config.jackson.BooleanSerializer;
import org.majimena.petical.config.jackson.ISO8601LocalDateDeserializer;
import org.majimena.petical.config.jackson.ISO8601LocalDateSerializer;
import org.majimena.petical.config.jackson.ISO8601LocalDateTimeDeserializer;
import org.majimena.petical.config.jackson.ISO8601LocalDateTimeSerializer;
import org.majimena.petical.config.jackson.ISO8601ZonedDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Jackson（RESTのJSONファクトリ）のコンフィグレーション.
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new Hibernate4Module(), new JavaTimeModule(), new CustomJacksonModule()).build();
    }

    @Bean
    public JsonFactory jsonFactory() {
        JacksonJsonFactory factory = new JacksonJsonFactory();
        factory.setObjectMapper(objectMapper());
        return factory;
    }

    private static class CustomJacksonModule extends SimpleModule {
        public CustomJacksonModule() {
            // add custom serializer for json
            addSerializer(LocalDate.class, new ISO8601LocalDateSerializer());
            addSerializer(LocalDateTime.class, new ISO8601LocalDateTimeSerializer());
            addSerializer(Boolean.class, new BooleanSerializer());
            addKeySerializer(ZonedDateTime.class, new ISO8601ZonedDateTimeSerializer());

            // add custom deserializer for json
            addDeserializer(LocalDate.class, new ISO8601LocalDateDeserializer());
            addDeserializer(LocalDateTime.class, new ISO8601LocalDateTimeDeserializer());
            addDeserializer(Boolean.class, new BooleanDeserializer());
        }
    }
}
