package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

public record ServerboundKeepAliveConfiguration(long id) implements ServerboundPacket {

    public static Format<ServerboundKeepAliveConfiguration> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.signedLong(), ServerboundKeepAliveConfiguration::id),
        ServerboundKeepAliveConfiguration::new
    );
}
