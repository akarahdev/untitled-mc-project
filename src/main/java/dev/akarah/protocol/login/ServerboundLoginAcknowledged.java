package dev.akarah.protocol.login;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

import java.util.UUID;

public record ServerboundLoginAcknowledged() implements ServerboundPacket {
    public static Format<ServerboundLoginAcknowledged> FORMAT = RecordFormat.ofRecord(
        ServerboundLoginAcknowledged::new
    );
}
