package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class CompoundTag extends NbtTag {
    HashMap<String, NbtTag> value = new HashMap<>();

    public CompoundTag() {

    }

    public CompoundTag(PacketBuf buf) {
        try {
            var entryTag = Tag.TAGS.get(buf.readByte());
            if(entryTag.equals(Tag.END))
                return;
            var nameLength = buf.readUnsignedShort();
            var nameArr = new byte[nameLength];
            for(int i = 0; i < nameLength; i++)
                nameArr[i] = buf.readByte();
            var name = new String(nameArr, StandardCharsets.UTF_8);
            var value = entryTag.getTypeClass().getConstructor(PacketBuf.class).newInstance(buf);
            this.value.put(name, value);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    public CompoundTag put(String key, NbtTag svalue) {
        this.value.put(key, svalue);
        return this;
    }

    public NbtTag get(String key) {
        return this.value.get(key);
    }

    @Override
    public Tag<CompoundTag> tag() {
        return Tag.COMPOUND;
    }

    @Override
    public void write(PacketBuf buffer) {
        for(var entryKey : value.keySet()) {
            var entryValue = value.get(entryKey);
            buffer.writeByte((byte) entryValue.tag().id());
            buffer.writeUnsignedShort((short) entryKey.getBytes(StandardCharsets.UTF_8).length);
            for(var b : entryKey.getBytes(StandardCharsets.UTF_8)) {
                buffer.writeByte(b);
            }
            entryValue.write(buffer);
        }
        buffer.writeByte((byte) 0);
    }

    @Override
    public int size() {
        return
            // string length + string prefix + value tag
            this.value.keySet().stream().mapToInt(it -> it.getBytes(StandardCharsets.UTF_8).length + 3).sum()
                // raw value size
            + this.value.values().stream().mapToInt(NbtTag::size).sum()
                // end tag byte
            + 1;
    }
}
