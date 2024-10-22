package dev.akarah.event;

import dev.akarah.protocol.Packet;

public record EventHandle<T extends Packet>(
    Class<T> packetClass,
    EventHandler<ServerboundPacketEvent<T>> packetHandler
) {
}
