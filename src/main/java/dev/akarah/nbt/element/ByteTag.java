package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

public class ByteTag extends NbtTag {
    byte value;

    public ByteTag(byte value) {
        this.value = value;
    }

    public ByteTag(PacketBuf buf) {
        this.value = buf.readByte();
    }

    @Override
    public Tag<ByteTag> tag() {
        return Tag.BYTE;
    }

    @Override
    public void write(PacketBuf buffer) {
        buffer.writeByte(this.value);
    }

    @Override
    public int size() {
        return Byte.BYTES;
    }
}
