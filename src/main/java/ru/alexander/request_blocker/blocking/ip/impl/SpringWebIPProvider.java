package ru.alexander.request_blocker.blocking.ip.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.alexander.request_blocker.blocking.ip.api.CurrentIPProvider;

import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;

@Service
class SpringWebIPProvider implements CurrentIPProvider {
    @Override
    public String getCurrentIPAddress() {
        return ((ServletRequestAttributes) currentRequestAttributes())
            .getRequest()
            .getRemoteAddr();
    }
}
