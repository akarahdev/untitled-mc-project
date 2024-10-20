package dev.akarah.protocol;

import dev.akarah.network.Format;
import dev.akarah.network.PacketBuf;

public interface Packet<T extends Packet<T>> {
    Format<T> format();
}
