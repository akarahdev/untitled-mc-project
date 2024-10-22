package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;

public record ClientboundFinishConfiguration() implements ClientboundPacket {
    public static Format<ClientboundFinishConfiguration> FORMAT = RecordFormat.ofRecord(
        ClientboundFinishConfiguration::new
    );
}
