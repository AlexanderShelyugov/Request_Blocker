package ru.alexander.request_blocker.blocking;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.alexander.request_blocker.blocking.storage.cleanup.CountersCleanupConfiguration;
import ru.alexander.request_blocker.blocking.storage.impl.CountersStorageConfiguration;

@Configuration
@ComponentScan
@Import({CountersStorageConfiguration.class, CountersCleanupConfiguration.class})
public class LimitIPConfiguration {
}
