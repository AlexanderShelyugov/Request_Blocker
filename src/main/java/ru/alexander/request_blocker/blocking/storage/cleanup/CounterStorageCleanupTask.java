package ru.alexander.request_blocker.blocking.storage.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterStorageOperations;

/**
 * Periodically cleans up counters storage
 */
@RequiredArgsConstructor
@Slf4j
class CounterStorageCleanupTask {
    private static final String CLEANUP_DELAY = "#{${block_ip.requests.time_window_seconds:5} * 1000 }";
    private final CommonCounterStorageOperations counterLogic;

    @Scheduled(
        initialDelayString = CLEANUP_DELAY,
        fixedRateString = CLEANUP_DELAY
    )
    public void cleanupCounters() {
        counterLogic.clearStorage();
        log.debug("Counters cleared");
    }
}
