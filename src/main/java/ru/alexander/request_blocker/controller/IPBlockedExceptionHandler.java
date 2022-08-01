package ru.alexander.request_blocker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.alexander.request_blocker.blocking.storage.api.exceptions.TooManyRequestsByIPException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
class IPBlockedExceptionHandler {
    private static final HttpStatus STATUS_TO_THROW = BAD_GATEWAY;

    @ResponseBody
    @ExceptionHandler(TooManyRequestsByIPException.class)
    public ResponseEntity<Object> tooManyIPRequests(TooManyRequestsByIPException e) {
        return status(STATUS_TO_THROW).build();
    }
}
