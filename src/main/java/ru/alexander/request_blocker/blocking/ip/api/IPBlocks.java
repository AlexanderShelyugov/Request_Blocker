package ru.alexander.request_blocker.blocking.ip.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks method to block execution if too many requests
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface IPBlocks {
}
