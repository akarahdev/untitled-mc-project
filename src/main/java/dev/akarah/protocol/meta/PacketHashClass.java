package dev.akarah.protocol.meta;

import dev.akarah.protocol.Packet;

public record PacketHashClass(
        Class<? extends Packet> packetClass,
        PacketFlow packetFlow,
        PacketStage packetStage
) {
    @Override
    public int hashCode() {
        return packetClass.getName().hashCode()
                * 31 * packetFlow.ordinal()
                * 31 * packetStage.ordinal();
    }
}
