package dev.akarah.protocol.meta;

public record PacketHashId(
        int packetId,
        PacketFlow packetFlow,
        PacketStage packetStage
) {
    @Override
    public int hashCode() {
        return packetId
                * 31 * packetFlow.ordinal()
                * 31 * packetStage.ordinal();
    }
}
