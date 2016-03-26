package org.majimena.petical.web.servlet.handler;

import cz.jirutka.spring.exhandler.handlers.ErrorMessageRestExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import java.util.Locale;

/**
 * Created by todoken on 2015/07/15.
 */
public abstract class AbstractCustomRestExceptionHandler<T extends Exception> extends ErrorMessageRestExceptionHandler<T> {

    public AbstractCustomRestExceptionHandler(Class<T> exceptionClass, HttpStatus status) {
        super(exceptionClass, status);
    }

    protected String getMessage(ObjectError error, Locale locale) {
        String message = super.getMessage(error.getCode(), locale);
        if (StringUtils.isEmpty(message)) {
            return error.getDefaultMessage();
        }
        return message;
    }
}
