package dev.akarah.registry;

import dev.akarah.network.PacketBuf;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.protocol.Packet;
import dev.akarah.protocol.configuration.ClientboundRegistryData;
import dev.akarah.registry.types.DimensionType;
import dev.akarah.server.ServerConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class RegistryView {
    HashMap<String, DimensionType> dimensionTypes = new HashMap<>();

    public RegistryView dimensionType(String name, DimensionType dimensionType) {
        this.dimensionTypes.put(name, dimensionType);
        return this;
    }

    public void sendToConnection(ServerConnection connection) {
        var dimensionTypeEntries = new ArrayList<ClientboundRegistryData.RegistryEntry>();
        for (var dt : dimensionTypes.entrySet()) {
            dimensionTypeEntries.add(new ClientboundRegistryData.RegistryEntry(
                dt.getKey(),
                Optional.of(dt.getValue().toNbt())
            ));
        }
        connection.send(new ClientboundRegistryData(
            "minecraft:dimension_type",
            dimensionTypeEntries
        ));
    }
}
