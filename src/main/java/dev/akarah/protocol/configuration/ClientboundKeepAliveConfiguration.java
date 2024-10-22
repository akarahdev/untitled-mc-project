package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;

public record ClientboundKeepAliveConfiguration(long id) implements ClientboundPacket {
    public static Format<ClientboundKeepAliveConfiguration> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.signedLong(), ClientboundKeepAliveConfiguration::id),
        ClientboundKeepAliveConfiguration::new
    );
}
