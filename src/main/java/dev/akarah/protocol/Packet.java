package dev.akarah.protocol;

import dev.akarah.network.Format;
import dev.akarah.util.ExceptionUtils;

/**
 * Represents an instance of a packet.
 * Must have a field named `FORMAT` of `Format<T>` where T is a type of itself.
 */
public interface Packet {
    default Format<? extends Packet> format() {
        var packetClass = this.getClass();
        var rawField = ExceptionUtils.moveToRuntime(() -> packetClass.getField("FORMAT").get(null));
        return (Format<? extends Packet>) rawField;
    }
}
