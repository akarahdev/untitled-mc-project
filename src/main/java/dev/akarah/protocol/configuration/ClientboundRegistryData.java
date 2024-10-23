package dev.akarah.protocol.configuration;

import dev.akarah.nbt.element.CompoundTag;
import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.registry.RegistryView;

import java.util.List;
import java.util.Optional;

public record ClientboundRegistryData(
        String identifier,
        List<RegistryEntry> entries
) implements ClientboundPacket {
    public record RegistryEntry(
        String entryName,
        Optional<CompoundTag> nbt
    ) {}

    public static Format<ClientboundRegistryData> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.string(), ClientboundRegistryData::identifier),
        RecordFormat.field(
            Format.arrayOf(
                RecordFormat.ofRecord(
                    RecordFormat.field(Format.string(), RegistryEntry::entryName),
                    RecordFormat.field(Format.optionalOf(Format.nbtCompound()), RegistryEntry::nbt),
                    RegistryEntry::new
                )
            ),
            ClientboundRegistryData::entries
        ),
        ClientboundRegistryData::new
    );
}
