package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

public class ShortTag extends NbtTag {
    short value;

    public ShortTag(short value) {
        this.value = value;
    }

    public ShortTag(PacketBuf buf) {
        this.value = buf.readShort();
    }

    @Override
    public Tag<ShortTag> tag() {
        return Tag.SHORT;
    }

    @Override
    public void write(PacketBuf buffer) {
        buffer.writeShort(this.value);
    }

    @Override
    public int size() {
        return 2;
    }
}
