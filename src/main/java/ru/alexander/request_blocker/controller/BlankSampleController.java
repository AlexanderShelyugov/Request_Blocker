package ru.alexander.request_blocker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;

@RestController
@Slf4j
class BlankSampleController {
    @GetMapping("/sample")
    public ResponseEntity<String> callSampleController() {
        String remoteAddress = ((ServletRequestAttributes) currentRequestAttributes())
            .getRequest().getRemoteAddr();
        log.info("We had request from {}", remoteAddress);
        return ok(remoteAddress);
    }
}
