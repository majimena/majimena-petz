package org.majimena.petical.common.exceptions;

import org.majimena.petical.domain.errors.ErrorCode;

/**
 * Created by todoken on 2015/07/13.
 */
public class ApplicationException extends RuntimeException {

    private ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ApplicationException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
