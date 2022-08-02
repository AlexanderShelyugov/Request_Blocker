package ru.alexander.request_blocker.blocking.ip.api.exceptions;

/**
 * Exception thrown in case of too many requests from one IP address
 */
public class TooManyRequestsByIPException extends ExecutionBlockException {
    public TooManyRequestsByIPException() {
        super();
    }

    public TooManyRequestsByIPException(String message) {
        super(message);
    }

    public TooManyRequestsByIPException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyRequestsByIPException(Throwable cause) {
        super(cause);
    }

    protected TooManyRequestsByIPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
