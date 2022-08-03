package ru.alexander.request_blocker.blocking.ip.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks method to block execution if too many requests.
 * <p>
 * If the method was called too many times from the same IP in the given period of time,
 * method will throw {@link ru.alexander.request_blocker.blocking.ip.api.exceptions.TooManyRequestsByIPException}.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface LimitSameIP {
}
