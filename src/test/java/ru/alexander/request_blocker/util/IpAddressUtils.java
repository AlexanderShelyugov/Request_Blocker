package ru.alexander.request_blocker.util;

import net.andreinc.mockneat.MockNeat;

public final class IpAddressUtils {

    private static final MockNeat GENERATOR = MockNeat.threadLocal();

    public static String randomIPAddress() {
        return GENERATOR.bools().val()
            ? randomIPv4Address()
            : randomIPv6Address();
    }

    public static String randomIPv4Address() {
        return GENERATOR.ipv4s().val();
    }

    public static String randomIPv6Address() {
        return GENERATOR.iPv6s().val();
    }

    private IpAddressUtils() {
    }
}
