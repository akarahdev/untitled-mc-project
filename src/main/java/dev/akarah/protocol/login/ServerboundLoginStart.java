package dev.akarah.protocol.login;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

import java.util.UUID;

public record ServerboundLoginStart(String username, UUID uuid) implements ServerboundPacket {
    public static Format<ServerboundLoginStart> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.string(), ServerboundLoginStart::username),
        RecordFormat.field(Format.uuid(), ServerboundLoginStart::uuid),
        ServerboundLoginStart::new
    );
}
