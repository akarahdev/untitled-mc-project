package dev.akarah.registry.types;

import dev.akarah.nbt.element.CompoundTag;

public interface RegistryType {
    CompoundTag toNbt();
}
