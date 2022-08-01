package ru.alexander.request_blocker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexander.request_blocker.blocking.ip.api.CurrentIPProvider;
import ru.alexander.request_blocker.blocking.ip.api.IPBlocks;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@Slf4j
@RequiredArgsConstructor
class BlankSampleController {
    private final CurrentIPProvider ipProvider;

    @GetMapping("/sample_ip_protected")
    @IPBlocks
    public ResponseEntity<String> ipProtectedEndpoint() {
        return ipUnprotectedEndpoint();
    }

    @GetMapping("/sample_ip_unprotected")
    @IPBlocks
    public ResponseEntity<String> ipUnprotectedEndpoint() {
        log.info("We had request from {}", ipProvider.getCurrentIPAddress());
        return ok("");
    }
}
