package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;

public record ClientboundPingConfiguration(int id) implements ClientboundPacket {
    public static Format<ClientboundPingConfiguration> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.signedInt(), ClientboundPingConfiguration::id),
        ClientboundPingConfiguration::new
    );
}
