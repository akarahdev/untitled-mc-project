package dev.akarah.registry.types;

import dev.akarah.nbt.element.*;

import java.util.Optional;

public record DimensionType(
    int minY,
    int height,

    boolean hasSkylight,
    boolean natural,

    BaseDimension baseDimension,
    float ambientLightLevel,
    boolean piglinSafe
) implements RegistryType {
    public enum BaseDimension {
        OVERWORLD("minecraft:overworld"),
        THE_NETHER("minecraft:the_nether"),
        THE_END("minecraft:the_end");

        final String value;

        BaseDimension(String value) {
            this.value = value;
        }
    }
    @Override
    public CompoundTag toNbt() {
        return new CompoundTag()
            .put("fixed_time", new LongTag(0))
            .put("has_skylight", new ByteTag(hasSkylight ? (byte) 1 : 0))
            .put("has_ceiling", new ByteTag((byte) 0))
            .put("ultrawarm", new ByteTag((byte) 0))
            .put("natural", new ByteTag(natural ? (byte) 1 : 0))
            .put("coordinate_scale", new DoubleTag(1))
            .put("bed_works", new ByteTag((byte) 0))
            .put("respawn_anchor_works", new ByteTag((byte) 0))
            .put("min_y", new IntTag(minY))
            .put("height", new IntTag(height))
            .put("logical_height", new IntTag(height))
            .put("infiniburn", new StringTag("#minecraft:overworld_infiniburn"))
            .put("effects", new StringTag(baseDimension.value))
            .put("ambient_light", new FloatTag(ambientLightLevel))
            .put("piglin_safe", new ByteTag(piglinSafe ? (byte) 1 : 0))
            .put("has_raids", new ByteTag((byte) 0))
            .put("monster_spawn_light_level", new IntTag(0))
            .put("monster_spawn_block_light_limit", new IntTag(0));
    }
}
