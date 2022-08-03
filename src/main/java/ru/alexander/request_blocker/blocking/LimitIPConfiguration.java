package ru.alexander.request_blocker.blocking;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Import this configuration to enable handling methods,
 * annotated with {@link ru.alexander.request_blocker.blocking.ip.api.LimitSameIP}.
 */
@Configuration
@ComponentScan
public class LimitIPConfiguration {
}
