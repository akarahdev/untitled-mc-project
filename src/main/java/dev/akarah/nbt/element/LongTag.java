package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

public class LongTag extends NbtTag {
    long value;

    public LongTag(long value) {
        this.value = value;
    }

    public LongTag(PacketBuf buf) {
        this.value = buf.readLong();
    }

    @Override
    public Tag<LongTag> tag() {
        return Tag.LONG;
    }

    @Override
    public void write(PacketBuf buffer) {
        buffer.writeLong(this.value);
    }

    @Override
    public int size() {
        return 8;
    }
}
