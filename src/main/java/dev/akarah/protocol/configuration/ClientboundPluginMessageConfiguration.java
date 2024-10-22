package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;

public record ClientboundPluginMessageConfiguration(
        String identifier,
        Byte[] payload
) implements ClientboundPacket {
    public static Format<ClientboundPluginMessageConfiguration> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.string(), ClientboundPluginMessageConfiguration::identifier),
        RecordFormat.field(Format.terminalArrayOf(Format.signedByte()), ClientboundPluginMessageConfiguration::payload),
        ClientboundPluginMessageConfiguration::new
    );
}
