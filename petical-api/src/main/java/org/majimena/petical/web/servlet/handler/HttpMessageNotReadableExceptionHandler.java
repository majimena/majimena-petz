package org.majimena.petical.web.servlet.handler;

import cz.jirutka.spring.exhandler.messages.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;

/**
 * HttpMessageNotReadableExceptionのハンドラ.
 */
public class HttpMessageNotReadableExceptionHandler extends AbstractCustomRestExceptionHandler<HttpMessageNotReadableException> {
    /**
     * ログ.
     */
    private static final Logger LOG = LoggerFactory.getLogger(HttpMessageNotReadableExceptionHandler.class);

    /**
     * コンストラクタ.
     */
    public HttpMessageNotReadableExceptionHandler() {
        super(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorMessage createBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
        LOG.warn("json to object deserialization failure", ex);
        return super.createBody(ex, req);
    }
}
