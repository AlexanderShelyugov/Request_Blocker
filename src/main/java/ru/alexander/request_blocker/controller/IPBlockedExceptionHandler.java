package ru.alexander.request_blocker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.alexander.request_blocker.blocking.storage.api.TooManyRequestsByIPException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ControllerAdvice
class IPBlockedExceptionHandler {
    @ResponseBody
    @ExceptionHandler(TooManyRequestsByIPException.class)
    public ResponseEntity<Object> tooManyIPRequests(TooManyRequestsByIPException e) {
        return ResponseEntity.status(BAD_GATEWAY).build();
    }
}
