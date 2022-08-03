package ru.alexander.request_blocker.blocking.ip.api;

/**
 * Provides callee IP address if possible
 */
public interface CurrentIPProvider {
    /**
     * @return callee IP address if it can be obtained, null otherwise
     */
    String getCurrentIPAddress();
}
