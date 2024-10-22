package dev.akarah.server;

import dev.akarah.event.EventHandle;
import dev.akarah.event.EventHandler;
import dev.akarah.event.ServerboundPacketEvent;
import dev.akarah.protocol.Packet;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    List<EventHandle<?>> serverboundPacketEvents = new ArrayList<>();

    public EventManager() {}

    public<T extends Packet> void registerEvent(Class<T> packetClass, EventHandler<ServerboundPacketEvent<T>> eventHandler) {
        this.serverboundPacketEvents.add(new EventHandle<>(packetClass, eventHandler));
    }
}
