package dev.akarah.protocol.status;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;
import dev.akarah.protocol.ServerboundPacketId;

public record ServerboundStatusRequest() implements ServerboundPacket {
    public static int PACKET_ID = ServerboundPacketId.packetId(ServerboundStatusRequest.class);

    public static Format<ServerboundStatusRequest> FORMAT = RecordFormat.ofRecord(
        ServerboundStatusRequest::new
    );
}
