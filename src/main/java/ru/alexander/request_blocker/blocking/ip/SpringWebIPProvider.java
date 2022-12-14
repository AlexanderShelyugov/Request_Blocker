package ru.alexander.request_blocker.blocking.ip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.alexander.request_blocker.blocking.ip.api.CurrentIPProvider;

import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;

/**
 * Allows to get callee IP address via Spring Web.
 */
@Service
@Slf4j
class SpringWebIPProvider implements CurrentIPProvider {
    @Override
    public String getCurrentIPAddress() {
        try {
            return ((ServletRequestAttributes) currentRequestAttributes())
                .getRequest()
                .getRemoteAddr();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
