package dev.akarah.protocol.status;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.protocol.PacketIdentifiers;

public record ClientboundStatusResponse() implements ClientboundPacket {
    public static int PACKET_ID = PacketIdentifiers.Clientbound.packetId(ClientboundStatusResponse.class);

    public static Format<ClientboundStatusResponse> FORMAT = RecordFormat.ofRecord(
        ClientboundStatusResponse::new
    );
}
