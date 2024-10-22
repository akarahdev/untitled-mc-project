package dev.akarah.protocol.configuration;

import dev.akarah.nbt.element.CompoundTag;
import dev.akarah.nbt.element.NbtTag;
import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;

public record ClientboundDisconnectConfiguration(
        CompoundTag reason
) implements ClientboundPacket {
    public static Format<ClientboundDisconnectConfiguration> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.nbtCompound(), ClientboundDisconnectConfiguration::reason),
        ClientboundDisconnectConfiguration::new
    );
}
