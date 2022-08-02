package ru.alexander.request_blocker.blocking.ip.api.exceptions;

public class UnableToGetIPException extends ExecutionBlockException {
    public UnableToGetIPException() {
        super();
    }

    public UnableToGetIPException(String message) {
        super(message);
    }

    public UnableToGetIPException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToGetIPException(Throwable cause) {
        super(cause);
    }

    protected UnableToGetIPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
