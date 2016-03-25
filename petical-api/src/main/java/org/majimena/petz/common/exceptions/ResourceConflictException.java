package org.majimena.petz.common.exceptions;

/**
 * Created by todoken on 2015/07/08.
 */
public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException() {
    }

    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceConflictException(Throwable cause) {
        super(cause);
    }

    public ResourceConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
