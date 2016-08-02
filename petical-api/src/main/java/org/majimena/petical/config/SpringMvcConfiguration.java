package org.majimena.petical.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jirutka.spring.exhandler.RestHandlerExceptionResolver;
import cz.jirutka.spring.exhandler.support.HttpMessageConverterUtils;
import org.majimena.petical.common.exceptions.ResourceConflictException;
import org.majimena.petical.common.exceptions.ResourceNotFoundException;
import org.majimena.petical.security.ResourceCannotAccessException;
import org.majimena.petical.web.servlet.handler.ApplicationExceptionRestExceptionHandler;
import org.majimena.petical.web.servlet.handler.BindExceptionRestExceptionHandler;
import org.majimena.petical.web.servlet.handler.HttpMessageNotReadableExceptionHandler;
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

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * SpringMVCのコンフィグレーション.
 */
@EnableWebMvc
@Configuration
public class SpringMvcConfiguration extends WebMvcConfigurerAdapter {

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(exceptionHandlerExceptionResolver()); // resolves @ExceptionHandler
        resolvers.add(restExceptionResolver());
    }

    @Bean
    public RestHandlerExceptionResolver restExceptionResolver() {
        return RestHandlerExceptionResolver.builder()
                .messageSource(messageSource())
                .defaultContentType(MediaType.APPLICATION_JSON)
                .addErrorMessageHandler(EmptyResultDataAccessException.class, HttpStatus.NOT_FOUND)
                .addErrorMessageHandler(ResourceNotFoundException.class, HttpStatus.NOT_FOUND)
                .addErrorMessageHandler(ResourceConflictException.class, HttpStatus.CONFLICT)
                .addErrorMessageHandler(ResourceCannotAccessException.class, HttpStatus.UNAUTHORIZED)
                .addHandler(new BindExceptionRestExceptionHandler())
                .addHandler(new ApplicationExceptionRestExceptionHandler(messageSource()))
                .addHandler(new HttpMessageNotReadableExceptionHandler())
                .build();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/i18n/messages", "classpath:/i18n/errors");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.setMessageConverters(HttpMessageConverterUtils.getDefaultHttpMessageConverters());
        return resolver;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
        jacksonConverter.setObjectMapper(objectMapper);
        return jacksonConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }
}
