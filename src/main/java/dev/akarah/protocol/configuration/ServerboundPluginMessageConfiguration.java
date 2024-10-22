package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

public record ServerboundPluginMessageConfiguration(
        String identifier,
        Byte[] payload
) implements ServerboundPacket {

    public static Format<ServerboundPluginMessageConfiguration> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.string(), ServerboundPluginMessageConfiguration::identifier),
        RecordFormat.field(Format.terminalArrayOf(Format.signedByte()), ServerboundPluginMessageConfiguration::payload),
        ServerboundPluginMessageConfiguration::new
    );
}
