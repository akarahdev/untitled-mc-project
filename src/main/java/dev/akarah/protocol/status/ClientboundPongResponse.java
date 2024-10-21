package dev.akarah.protocol.status;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.protocol.PacketIdentifiers;

public record ClientboundPongResponse(long value) implements ClientboundPacket {
    public static int PACKET_ID = PacketIdentifiers.Clientbound.packetId(ClientboundPongResponse.class);

    public static Format<ClientboundPongResponse> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.signedLong(), ClientboundPongResponse::value),
        ClientboundPongResponse::new
    );
}
