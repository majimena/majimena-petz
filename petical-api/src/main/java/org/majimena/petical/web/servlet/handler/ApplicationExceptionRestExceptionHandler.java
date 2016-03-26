package org.majimena.petical.web.servlet.handler;

import cz.jirutka.spring.exhandler.messages.ErrorMessage;
import cz.jirutka.spring.exhandler.messages.ValidationErrorMessage;
import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.domain.errors.ErrorCode;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by todoken on 2015/07/15.
 */
public class ApplicationExceptionRestExceptionHandler extends AbstractCustomRestExceptionHandler<ApplicationException> {

    public ApplicationExceptionRestExceptionHandler() {
        super(ApplicationException.class, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ErrorMessage createBody(ApplicationException ex, HttpServletRequest req) {
        ErrorMessage original = super.createBody(ex, req);
        ValidationErrorMessage message = new ValidationErrorMessage(original);

        ErrorCode code = ex.getErrorCode();
        message.addError(getMessage(code.name(), req.getLocale()));

        return message;
    }
}
