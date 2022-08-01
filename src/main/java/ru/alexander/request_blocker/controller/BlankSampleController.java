package ru.alexander.request_blocker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
class BlankSampleController {
    @GetMapping("/sample")
    public ResponseEntity<String> callSampleController() {
        return ok("");
    }
}
