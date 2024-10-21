package dev.akarah.protocol;

import dev.akarah.protocol.meta.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketIdentifiers {
    final static AtomicPacketIdTracker SERVERBOUND_ID;
    final static AtomicPacketIdTracker CLIENTBOUND_ID;

    final static ConcurrentHashMap<PacketHashId, PacketHashClass> ID_TO_CLASS;
    final static ConcurrentHashMap<PacketHashClass, PacketHashId> CLASS_TO_ID;

    static {
        SERVERBOUND_ID = new AtomicPacketIdTracker(
                new AtomicInteger(0),
                new AtomicInteger(0),
                new AtomicInteger(0),
                new AtomicInteger(0),
                new AtomicInteger(0)
        );
        CLIENTBOUND_ID = new AtomicPacketIdTracker(
                new AtomicInteger(0),
                new AtomicInteger(0),
                new AtomicInteger(0),
                new AtomicInteger(0),
                new AtomicInteger(0)
        );

        ID_TO_CLASS = new ConcurrentHashMap<>();
        CLASS_TO_ID = new ConcurrentHashMap<>();
    }

    static void registerPacket(
            Class<Packet> packetClass,
            PacketFlow flow,
            PacketStage stage
    ) {
        var id = switch (flow) {
            case SERVERBOUND -> switch (stage) {
                case HANDSHAKING -> SERVERBOUND_ID.handshaking().getAndIncrement();
                case STATUS -> SERVERBOUND_ID.status().getAndIncrement();
                case LOGIN -> SERVERBOUND_ID.login().getAndIncrement();
                case CONFIGURATION -> SERVERBOUND_ID.configuration().getAndIncrement();
                case PLAY -> SERVERBOUND_ID.play().getAndIncrement();
            };
            case CLIENTBOUND -> switch (stage) {
                case HANDSHAKING -> CLIENTBOUND_ID.handshaking().getAndIncrement();
                case STATUS -> CLIENTBOUND_ID.status().getAndIncrement();
                case LOGIN -> CLIENTBOUND_ID.login().getAndIncrement();
                case CONFIGURATION -> CLIENTBOUND_ID.configuration().getAndIncrement();
                case PLAY -> CLIENTBOUND_ID.play().getAndIncrement();
            };
        };
        CLASS_TO_ID.put(
                new PacketHashClass(packetClass, flow, stage),
                new PacketHashId(id, flow, stage)
        );
        ID_TO_CLASS.put(
                new PacketHashId(id, flow, stage),
                new PacketHashClass(packetClass, flow, stage)
        );
    }
}
