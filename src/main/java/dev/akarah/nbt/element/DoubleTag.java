package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

// todo: fix double tag causing off-by-one error
public class DoubleTag extends NbtTag {
    double value;

    public DoubleTag(double value) {
        this.value = value;
    }

    public DoubleTag(PacketBuf buf) {
        this.value = buf.readDouble();
    }

    @Override
    public Tag<DoubleTag> tag() {
        return Tag.DOUBLE;
    }

    @Override
    public void write(PacketBuf buffer) {
        buffer.writeDouble(this.value);
    }

    @Override
    public int size() {
        return Double.BYTES;
    }
}
