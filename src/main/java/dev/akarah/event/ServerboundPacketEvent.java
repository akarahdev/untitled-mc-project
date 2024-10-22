package dev.akarah.event;

import dev.akarah.protocol.Packet;
import dev.akarah.server.ServerConnection;

public record ServerboundPacketEvent<T extends Packet>(
    ServerConnection connection,
    T packet
) {
}
