package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

public class ByteArrayTag extends NbtTag {
    byte[] value;

    public ByteArrayTag(byte[] value) {
        this.value = value;
    }

    public ByteArrayTag(PacketBuf buf) {
        var length = buf.readInt();
        this.value = new byte[length];
        for(int i = 0; i < length; i ++)
            this.value[i] = buf.readByte();
    }

    @Override
    public Tag<ByteArrayTag> tag() {
        return Tag.BYTE_ARRAY;
    }

    @Override
    public void write(PacketBuf buffer) {
        buffer.writeInt(this.value.length);
        for(var b : this.value)
            buffer.writeByte(b);
    }

    @Override
    public int size() {
        return 4 + this.value.length;
    }
}
