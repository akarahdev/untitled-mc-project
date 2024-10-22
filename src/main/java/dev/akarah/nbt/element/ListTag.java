package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ListTag<T extends NbtTag> extends NbtTag {
    T[] value;
    Tag<T> subtype;

    public ListTag(Tag<T> subtype, T[] value) {
        this.value = value;
        this.subtype = subtype;
    }

    public ListTag(PacketBuf buf) {
        constructLogic((Tag<T>) Tag.TAGS.get(buf.readByte()), buf);
    }

    public ListTag(Tag<T> subtype, PacketBuf buf) {
        constructLogic(subtype, buf);
    }

    private void constructLogic(Tag<T> subtype, PacketBuf buf) {
        this.subtype = subtype;
        var length = buf.readInt();
        this.value = (T[]) Array.newInstance(subtype.getTypeClass(), length);

        for(int i = 0; i < length; i++) {
            try {
                this.value[i] = subtype.getTypeClass().getConstructor(PacketBuf.class).newInstance(buf);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    @Override
    public Tag<ListTag<T>> tag() {
        return new Tag.ListType<T>();
    }

    @Override
    public void write(PacketBuf buffer) {
        buffer.writeByte((byte) subtype.id());
        buffer.writeInt(this.value.length);
        for(var inner : this.value) {
            inner.write(buffer);
        }
    }

    @Override
    public int size() {
        return
            // total values
            Arrays.stream(this.value).mapToInt(NbtTag::size).sum()
                // type id byte
                + 1
                // length bytes
                + 4;
    }
}
