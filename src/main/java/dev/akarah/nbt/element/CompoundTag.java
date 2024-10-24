package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class CompoundTag extends NbtTag {
    HashMap<String, NbtTag> value = new HashMap<>();

    public CompoundTag() {

    }

    public CompoundTag(PacketBuf buf) {
        try {
            while(true) {
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
            }
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
            System.out.println("k: " + entryKey + " v: " + entryValue);
            System.out.println("pre-write: " + Arrays.toString(buffer.toArray()));
            buffer.writeByte((byte) entryValue.tag().id());
            new StringTag(entryKey).write(buffer);
            System.out.println("post-key write: " + Arrays.toString(buffer.toArray()));
            entryValue.write(buffer);
            System.out.println("post-value write: " + Arrays.toString(buffer.toArray()));

        }
        buffer.writeByte((byte) Tag.END.id());
    }

    @Override
    public int size() {
        return
            // string length + string forced prefix
            this.value.keySet().stream().mapToInt(it -> new StringTag(it).size()).sum()
                // raw value size + value tag
            + this.value.values().stream().mapToInt(it -> it.size() + 1).sum()
                // end tag
            + 1;
    }
}
