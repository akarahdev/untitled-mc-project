package dev.akarah.protocol.meta;

import java.util.concurrent.atomic.AtomicInteger;

public record AtomicPacketIdTracker(
        AtomicInteger handshaking,
        AtomicInteger status,
        AtomicInteger login,
        AtomicInteger configuration,
        AtomicInteger play
) {
}
