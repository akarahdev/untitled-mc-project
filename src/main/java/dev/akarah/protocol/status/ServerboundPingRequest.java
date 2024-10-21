package dev.akarah.protocol.status;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;
import dev.akarah.protocol.ServerboundPacketId;

public record ServerboundPingRequest(long value) implements ServerboundPacket {
    public static int PACKET_ID = ServerboundPacketId.packetId(ServerboundPingRequest.class);

    public static Format<ServerboundPingRequest> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.signedLong(), ServerboundPingRequest::value),
        ServerboundPingRequest::new
    );
}
