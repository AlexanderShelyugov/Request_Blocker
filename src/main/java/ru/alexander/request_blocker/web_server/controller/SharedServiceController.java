package ru.alexander.request_blocker.web_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexander.request_blocker.web_server.service.api.SomeProtectedService;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
class SharedServiceController {
    private final SomeProtectedService service;

    @GetMapping("/shared_service_a")
    public ResponseEntity<String> sharedServiceA() {
        service.someProtectedMethod();
        return ok().build();
    }

    @GetMapping("/shared_service_b")
    public ResponseEntity<String> sharedServiceB() {
        service.someProtectedMethod();
        return ok().build();
    }
}
