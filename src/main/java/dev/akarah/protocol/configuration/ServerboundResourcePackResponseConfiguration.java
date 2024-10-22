package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

import java.util.UUID;

public record ServerboundResourcePackResponseConfiguration(UUID uuid, int responseCode) implements ServerboundPacket {

    public static Format<ServerboundResourcePackResponseConfiguration> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.uuid(), ServerboundResourcePackResponseConfiguration::uuid),
        RecordFormat.field(Format.varInt(), ServerboundResourcePackResponseConfiguration::responseCode),
        ServerboundResourcePackResponseConfiguration::new
    );
}
