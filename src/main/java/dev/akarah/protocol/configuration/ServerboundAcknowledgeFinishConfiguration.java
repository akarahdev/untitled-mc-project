package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

public record ServerboundAcknowledgeFinishConfiguration() implements ServerboundPacket {

    public static Format<ServerboundAcknowledgeFinishConfiguration> FORMAT = RecordFormat.ofRecord(
        ServerboundAcknowledgeFinishConfiguration::new
    );
}
