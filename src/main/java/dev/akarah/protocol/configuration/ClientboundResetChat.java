package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;

public record ClientboundResetChat() implements ClientboundPacket {
    public static Format<ClientboundResetChat> FORMAT = RecordFormat.ofRecord(
        ClientboundResetChat::new
    );
}
