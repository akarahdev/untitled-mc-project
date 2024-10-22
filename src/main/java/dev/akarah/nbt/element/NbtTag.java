package dev.akarah.nbt.element;

import dev.akarah.network.PacketBuf;

public abstract class NbtTag {
    public abstract Tag<?> tag();
    public abstract void write(PacketBuf buffer);
    public abstract int size();
}
