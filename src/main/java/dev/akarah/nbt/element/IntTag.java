package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

public class IntTag extends NbtTag {
    int value;

    public IntTag(int value) {
        this.value = value;
    }

    public IntTag(PacketBuf buf) {
        this.value = buf.readInt();
    }

    @Override
    public Tag<IntTag> tag() {
        return Tag.INTEGER;
    }

    @Override
    public void write(PacketBuf buffer) {
        buffer.writeInt(this.value);
    }

    @Override
    public int size() {
        return 4;
    }
}
