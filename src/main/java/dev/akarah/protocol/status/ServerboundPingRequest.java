package dev.akarah.protocol.status;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.PacketIdentifiers;
import dev.akarah.protocol.ServerboundPacket;

public record ServerboundPingRequest(long value) implements ServerboundPacket {
    public static Format<ServerboundPingRequest> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.signedLong(), ServerboundPingRequest::value),
        ServerboundPingRequest::new
    );
}
