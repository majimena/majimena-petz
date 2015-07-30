package org.majimena.petz.config;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.JacksonJodaFormat;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.joda.time.DateTime;
import org.majimena.framework.core.datatypes.converters.ISO8601LocalDateDeserializer;
import org.majimena.framework.core.datatypes.converters.ISO8601LocalDateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;

import java.time.LocalDate;

@Configuration
public class JacksonConfiguration {

    @Bean
    public JodaModule jacksonJodaModule() {
        JodaModule module = new JodaModule();
        DateTimeFormatterFactory formatterFactory = new DateTimeFormatterFactory();
        formatterFactory.setIso(DateTimeFormat.ISO.DATE);
        module.addSerializer(DateTime.class, new DateTimeSerializer(
                new JacksonJodaFormat(formatterFactory.createDateTimeFormatter()
                        .withZoneUTC())));
        return module;
    }

    @Bean
    public JSR310Module jsr310Module() {
        JSR310Module module = new JSR310Module();
        module.addSerializer(LocalDate.class, new ISO8601LocalDateSerializer());
        module.addDeserializer(LocalDate.class, new ISO8601LocalDateDeserializer());
        return module;
    }

}
