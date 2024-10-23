package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

public class FloatTag extends NbtTag {
    float value;

    public FloatTag(float value) {
        this.value = value;
    }

    public FloatTag(PacketBuf buf) {
        this.value = buf.readFloat();
    }

    @Override
    public Tag<FloatTag> tag() {
        return Tag.FLOAT;
    }

    @Override
    public void write(PacketBuf buffer) {
        buffer.writeFloat(this.value);
    }

    @Override
    public int size() {
        return Float.BYTES;
    }
}
