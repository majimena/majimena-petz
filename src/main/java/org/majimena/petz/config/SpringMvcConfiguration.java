package org.majimena.petz.config;

import cz.jirutka.spring.exhandler.RestHandlerExceptionResolver;
import cz.jirutka.spring.exhandler.support.HttpMessageConverterUtils;
import org.majimena.petz.common.exceptions.ResourceCannotAccessException;
import org.majimena.petz.common.exceptions.ResourceConflictException;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.web.servlet.handler.ApplicationExceptionRestExceptionHandler;
import org.majimena.petz.web.servlet.handler.BindExceptionRestExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.List;

/**
 * Created by todoken on 2015/06/25.
 */
@EnableWebMvc
@Configuration
public class SpringMvcConfiguration extends WebMvcConfigurerAdapter {

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
}
