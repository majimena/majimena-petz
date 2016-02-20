package org.majimena.petz.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import cz.jirutka.spring.exhandler.RestHandlerExceptionResolver;
import cz.jirutka.spring.exhandler.support.HttpMessageConverterUtils;
import org.majimena.petz.common.exceptions.ResourceConflictException;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.common.factory.JacksonJsonFactory;
import org.majimena.petz.common.factory.JsonFactory;
import org.majimena.petz.datatype.EnumDataType;
import org.majimena.petz.datatype.SexType;
import org.majimena.petz.datatype.deserializers.ISO8601LocalDateDeserializer;
import org.majimena.petz.datatype.deserializers.ISO8601LocalDateTimeDeserializer;
import org.majimena.petz.datatype.deserializers.SexTypeDeserializer;
import org.majimena.petz.datatype.serializers.EnumDataTypeSerializer;
import org.majimena.petz.datatype.serializers.ISO8601LocalDateSerializer;
import org.majimena.petz.datatype.serializers.ISO8601LocalDateTimeSerializer;
import org.majimena.petz.security.ResourceCannotAccessException;
import org.majimena.petz.web.servlet.handler.ApplicationExceptionRestExceptionHandler;
import org.majimena.petz.web.servlet.handler.BindExceptionRestExceptionHandler;
import org.majimena.petz.web.servlet.handler.HttpMessageNotReadableExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Created by todoken on 2015/06/25.
 */
@EnableWebMvc
@Configuration
public class SpringMvcConfiguration extends WebMvcConfigurerAdapter {

    // ExceptionHandler Configurations

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(exceptionHandlerExceptionResolver()); // resolves @ExceptionHandler
        resolvers.add(restExceptionResolver());
    }

    @Bean
    public RestHandlerExceptionResolver restExceptionResolver() {
        return RestHandlerExceptionResolver.builder()
                .messageSource(httpErrorMessageSource())
                .defaultContentType(MediaType.APPLICATION_JSON)
                .addErrorMessageHandler(EmptyResultDataAccessException.class, HttpStatus.NOT_FOUND)
                .addErrorMessageHandler(ResourceNotFoundException.class, HttpStatus.NOT_FOUND)
                .addErrorMessageHandler(ResourceConflictException.class, HttpStatus.CONFLICT)
                .addErrorMessageHandler(ResourceCannotAccessException.class, HttpStatus.UNAUTHORIZED)
                .addHandler(new BindExceptionRestExceptionHandler())
                .addHandler(applicationExceptionRestExceptionHandler())
                .addHandler(new HttpMessageNotReadableExceptionHandler())
                .build();
    }

    public ApplicationExceptionRestExceptionHandler applicationExceptionRestExceptionHandler() {
        ApplicationExceptionRestExceptionHandler handler = new ApplicationExceptionRestExceptionHandler();
        handler.setMessageSource(httpErrorMessageSource());
        return handler;
    }

    @Bean
    public MessageSource httpErrorMessageSource() {
        ReloadableResourceBundleMessageSource m = new ReloadableResourceBundleMessageSource();
        m.setBasenames("classpath:/i18n/messages", "classpath:/i18n/errors");
        m.setDefaultEncoding("UTF-8");
        return m;
    }

    @Bean
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.setMessageConverters(HttpMessageConverterUtils.getDefaultHttpMessageConverters());
        return resolver;
    }

    // Jackson Configurations

    @Inject
    private Hibernate4Module hibernate4Module;

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(Arrays.asList(jsr310Module(), hibernate4Module)).build();
    }

    @Bean
    public JSR310Module jsr310Module() {
        JSR310Module module = new JSR310Module();

        module.addSerializer(LocalDate.class, new ISO8601LocalDateSerializer());
        module.addSerializer(LocalDateTime.class, new ISO8601LocalDateTimeSerializer());
        module.addSerializer(EnumDataType.class, new EnumDataTypeSerializer());

        module.addDeserializer(LocalDate.class, new ISO8601LocalDateDeserializer());
        module.addDeserializer(LocalDateTime.class, new ISO8601LocalDateTimeDeserializer());
        module.addDeserializer(SexType.class, new SexTypeDeserializer());

        return module;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
        jacksonConverter.setObjectMapper(objectMapper());
        return jacksonConverter;
    }

    @Bean
    public JsonFactory jsonFactory() {
        JacksonJsonFactory factory = new JacksonJsonFactory();
        factory.setObjectMapper(objectMapper());
        return factory;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }
}
