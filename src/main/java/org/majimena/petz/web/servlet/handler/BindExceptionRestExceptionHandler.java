package org.majimena.petz.web.servlet.handler;

import cz.jirutka.spring.exhandler.messages.ErrorMessage;
import cz.jirutka.spring.exhandler.messages.ValidationErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by todoken on 2015/06/25.
 */
public class BindExceptionRestExceptionHandler extends AbstractCustomRestExceptionHandler<BindException> {

    public BindExceptionRestExceptionHandler() {
        super(BindException.class, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ErrorMessage createBody(BindException ex, HttpServletRequest req) {
        ErrorMessage original = super.createBody(ex, req);
        ValidationErrorMessage message = new ValidationErrorMessage(original);

        // collect field errors
        ex.getFieldErrors().stream()
            .forEach(error -> message.addError(error.getField(), error.getRejectedValue(), getMessage(error, req.getLocale())));
        // collect global errors
        ex.getGlobalErrors().stream()
            .forEach(error -> message.addError(getMessage(error, req.getLocale())));

        return message;
    }
}
