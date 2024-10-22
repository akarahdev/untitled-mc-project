package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.protocol.ServerboundPacket;

public record ClientboundCookieRequest(
        String identifier
) implements ClientboundPacket {
    public static Format<ClientboundCookieRequest> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.string(), ClientboundCookieRequest::identifier),
        ClientboundCookieRequest::new
    );
}
