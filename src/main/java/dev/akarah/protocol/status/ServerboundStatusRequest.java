package dev.akarah.protocol.status;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.PacketIdentifiers;
import dev.akarah.protocol.ServerboundPacket;

public record ServerboundStatusRequest() implements ServerboundPacket {
    public static Format<ServerboundStatusRequest> FORMAT = RecordFormat.ofRecord(
        ServerboundStatusRequest::new
    );
}
