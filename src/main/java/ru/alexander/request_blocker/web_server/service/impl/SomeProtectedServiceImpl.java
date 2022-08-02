package ru.alexander.request_blocker.web_server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alexander.request_blocker.blocking.ip.api.LimitSameIP;
import ru.alexander.request_blocker.web_server.service.api.SomeProtectedService;

@Service
@Slf4j
class SomeProtectedServiceImpl implements SomeProtectedService {
    // Method annotations are not inherited, therefore we add it on implementation.
    @Override
    @LimitSameIP
    public void someProtectedMethod() {
        log.info("Protected method was called");
    }
}
