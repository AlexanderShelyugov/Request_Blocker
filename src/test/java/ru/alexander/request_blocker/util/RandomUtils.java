package ru.alexander.request_blocker.util;

import org.jeasy.random.EasyRandom;

public final class RandomUtils {
    public static final EasyRandom RANDOM = new EasyRandom();

    public static String randomString() {
        return RANDOM.nextObject(String.class);
    }

    private RandomUtils() {
    }
}
