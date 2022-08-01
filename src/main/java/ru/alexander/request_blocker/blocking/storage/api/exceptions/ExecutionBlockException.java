package ru.alexander.request_blocker.blocking.storage.api.exceptions;

public class ExecutionBlockException extends RuntimeException {
    public ExecutionBlockException() {
        super();
    }

    public ExecutionBlockException(String message) {
        super(message);
    }

    public ExecutionBlockException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionBlockException(Throwable cause) {
        super(cause);
    }

    protected ExecutionBlockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
