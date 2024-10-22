package dev.akarah.protocol.status;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.protocol.PacketIdentifiers;

public record ClientboundStatusResponse(String json) implements ClientboundPacket {
    public static Format<ClientboundStatusResponse> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.string(), ClientboundStatusResponse::json),
        ClientboundStatusResponse::new
    );
}
