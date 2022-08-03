package ru.alexander.request_blocker.blocking.storage.sharding.strategy;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.ipv4.IPv4Address;
import inet.ipaddr.ipv4.IPv4AddressSection;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.validator.routines.InetAddressValidator;

/**
 * Sharding strategy that routes naming decision to underlying strategies depending on the given IP address version.
 * <p>
 * It also detects IPv4 addresses, written in IPv6 formats.
 */
@Builder
public class IPTypesShardingStrategy implements ShardingStrategy {
    private static final InetAddressValidator IP_VALIDATOR = InetAddressValidator.getInstance();
    @NonNull
    private final ShardingStrategy ipv4ShardingStrategy;
    @NonNull
    private final ShardingStrategy ipv6ShardingStrategy;

    @Override
    @SneakyThrows
    public String getShardName(int executionID, String ip) {
        if (!IP_VALIDATOR.isValid(ip)) {
            throw new IllegalArgumentException(
                String.format("Unable to understand address %s", ip));
        }
        final String shardName;
        ip = convertIPv6ToIPv4Maybe(ip);
        if (IP_VALIDATOR.isValidInet4Address(ip)) {
            shardName = ipv4ShardingStrategy.getShardName(executionID, ip);
        } else if (IP_VALIDATOR.isValidInet6Address(ip)) {
            shardName = ipv6ShardingStrategy.getShardName(executionID, ip);
        } else {
            throw new IllegalStateException(
                String.format("Unable to understand address %s", ip));
        }
        return shardName;
    }

    private String convertIPv6ToIPv4Maybe(String ip) throws AddressStringException {
        if (!IP_VALIDATOR.isValidInet6Address(ip)) {
            return ip;
        }
        val ipv6 = new IPAddressString(ip).toAddress().toIPv6();
        IPv4Address result;
        if (ipv6.isTeredo()) {
            IPv4AddressSection section = new IPv4AddressSection(~ipv6.getEmbeddedIPv4Address().intValue());
            result = ipv6.getIPv4Network().getAddressCreator().createAddress(section);
        } else if (ipv6.is6To4()) {
            result = ipv6.get6To4IPv4Address();
        } else if (ipv6.is6Over4() ||
            ipv6.isIsatap() ||
            ipv6.isIPv4Translatable() ||
            ipv6.isIPv4Mapped()
        ) {
            result = ipv6.getEmbeddedIPv4Address();
        } else if (ipv6.isLoopback()) {
            result = ipv6.getIPv4Network().getLoopback();
        } else {
            return ip;
        }
        return result.toCompressedString();
    }
}
