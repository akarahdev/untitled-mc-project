package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

import java.util.List;
import java.util.UUID;

public record ServerboundKnownPacks(List<KnownPack> knownPacks) implements ServerboundPacket {
    public record KnownPack(
            String namespace,
            String id,
            String version
    ) {
        public static Format<KnownPack> FORMAT = RecordFormat.ofRecord(
                RecordFormat.field(Format.string(), KnownPack::namespace),
                RecordFormat.field(Format.string(), KnownPack::id),
                RecordFormat.field(Format.string(), KnownPack::version),
                KnownPack::new
        );
    }
    public static Format<ServerboundKnownPacks> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.arrayOf(KnownPack.FORMAT), ServerboundKnownPacks::knownPacks),
        ServerboundKnownPacks::new
    );
}
