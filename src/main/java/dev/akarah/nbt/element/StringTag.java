package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

import java.nio.charset.StandardCharsets;

public class StringTag extends NbtTag {
    String value;

    public StringTag(String value) {
        this.value = value;
    }

    public StringTag(PacketBuf buf) {
        var length = buf.readUnsignedShort();
        var arr = new byte[length];
        for(int i = 0; i < length; i++)
            arr[i] = buf.readByte();
        this.value = new String(arr, StandardCharsets.UTF_8);
    }

    @Override
    public Tag<StringTag> tag() {
        return Tag.STRING;
    }

    @Override
    public void write(PacketBuf buffer) {
        buffer.writeUnsignedShort(this.value.getBytes(StandardCharsets.UTF_8).length);
        for(var b : this.value.getBytes(StandardCharsets.UTF_8))
            buffer.writeByte(b);
    }

    @Override
    public int size() {
        return
            // actual string
            this.value.getBytes(StandardCharsets.UTF_8).length
            // length prefix
            + Short.BYTES;
    }
}
