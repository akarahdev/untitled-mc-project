package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

public record ServerboundPongConfiguration(int id) implements ServerboundPacket {

    public static Format<ServerboundPongConfiguration> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.signedInt(), ServerboundPongConfiguration::id),
        ServerboundPongConfiguration::new
    );
}
