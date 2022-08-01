package ru.alexander.request_blocker.blocking.storage.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterLogic;

@Component
@RequiredArgsConstructor
@Slf4j
class CounterStorageCleanupTask {
    private static final long RATE = 10000;
    private final CommonCounterLogic counterLogic;

    @Scheduled(fixedRate = RATE)
    public void cleanupCounters() {
        counterLogic.clearStorage();
        log.info("Counters cleared");
    }
}
