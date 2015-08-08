package org.majimena.petz.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import cz.jirutka.spring.exhandler.RestHandlerExceptionResolver;
import cz.jirutka.spring.exhandler.support.HttpMessageConverterUtils;
import org.majimena.framework.beans.factory.JacksonJsonFactory;
import org.majimena.framework.beans.factory.JsonFactory;
import org.majimena.framework.core.datatypes.EnumDataType;
import org.majimena.framework.core.datatypes.converters.EnumDataTypeSerializer;
import org.majimena.framework.core.datatypes.converters.ISO8601LocalDateDeserializer;
import org.majimena.framework.core.datatypes.converters.ISO8601LocalDateSerializer;
import org.majimena.framework.core.datatypes.converters.ISO8601LocalDateTimeDeserializer;
import org.majimena.framework.core.datatypes.converters.ISO8601LocalDateTimeSerializer;
import org.majimena.petz.common.exceptions.ResourceCannotAccessException;
import org.majimena.petz.common.exceptions.ResourceConflictException;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.datatypes.SexType;
import org.majimena.petz.datatypes.SexTypeDeserializer;
import org.majimena.petz.web.servlet.handler.ApplicationExceptionRestExceptionHandler;
import org.majimena.petz.web.servlet.handler.BindExceptionRestExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

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

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(configureSimpleModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @Bean
    public SimpleModule configureSimpleModule() {
        SimpleModule module = new SimpleModule("PetzioModule");

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
        jacksonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.valueOf("application/json")));
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
