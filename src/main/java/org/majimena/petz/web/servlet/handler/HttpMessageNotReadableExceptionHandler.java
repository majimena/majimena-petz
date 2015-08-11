package org.majimena.petz.web.servlet.handler;

import cz.jirutka.spring.exhandler.messages.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by todoken on 2015/08/12.
 */
public class HttpMessageNotReadableExceptionHandler extends AbstractCustomRestExceptionHandler<HttpMessageNotReadableException> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpMessageNotReadableExceptionHandler.class);

    public HttpMessageNotReadableExceptionHandler() {
        super(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ErrorMessage createBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
        LOG.warn("", ex);
        return super.createBody(ex, req);
    }
}
