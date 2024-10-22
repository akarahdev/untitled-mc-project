package dev.akarah.protocol.login;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;

public record ClientboundDisconnectLogin(String reason) implements ClientboundPacket {
    public static Format<ClientboundDisconnectLogin> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.string(), ClientboundDisconnectLogin::reason),
        ClientboundDisconnectLogin::new
    );
}
