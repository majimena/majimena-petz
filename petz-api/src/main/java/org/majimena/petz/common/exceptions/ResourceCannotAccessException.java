package org.majimena.petz.common.exceptions;

/**
 * Created by todoken on 2015/07/08.
 */
public class ResourceCannotAccessException extends RuntimeException {

    public ResourceCannotAccessException() {
    }

    public ResourceCannotAccessException(String message) {
        super(message);
    }

    public ResourceCannotAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceCannotAccessException(Throwable cause) {
        super(cause);
    }

    public ResourceCannotAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
