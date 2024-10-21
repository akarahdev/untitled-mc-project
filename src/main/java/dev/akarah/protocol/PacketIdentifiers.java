package dev.akarah.protocol;

import dev.akarah.protocol.handshaking.ServerboundHandshake;
import dev.akarah.protocol.meta.*;
import dev.akarah.protocol.status.ClientboundPongResponse;
import dev.akarah.protocol.status.ClientboundStatusResponse;
import dev.akarah.protocol.status.ServerboundPingRequest;
import dev.akarah.protocol.status.ServerboundStatusRequest;

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

        registerSPacket(ServerboundHandshake.class, PacketStage.HANDSHAKING);

        registerCPacket(ClientboundStatusResponse.class, PacketStage.STATUS);
        registerCPacket(ClientboundPongResponse.class, PacketStage.STATUS);
        registerSPacket(ServerboundPingRequest.class, PacketStage.STATUS);
        registerSPacket(ServerboundStatusRequest.class, PacketStage.STATUS);
    }

    static void registerPacket(
            Class<? extends Packet> packetClass,
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

    static void registerSPacket(
            Class<? extends ServerboundPacket> packetClass,
            PacketStage stage
    ) {
        var id = switch (stage) {
            case HANDSHAKING -> SERVERBOUND_ID.handshaking().getAndIncrement();
            case STATUS -> SERVERBOUND_ID.status().getAndIncrement();
            case LOGIN -> SERVERBOUND_ID.login().getAndIncrement();
            case CONFIGURATION -> SERVERBOUND_ID.configuration().getAndIncrement();
            case PLAY -> SERVERBOUND_ID.play().getAndIncrement();
        };
        CLASS_TO_ID.put(
                new PacketHashClass(packetClass, PacketFlow.SERVERBOUND, stage),
                new PacketHashId(id, PacketFlow.SERVERBOUND, stage)
        );
        ID_TO_CLASS.put(
                new PacketHashId(id, PacketFlow.SERVERBOUND, stage),
                new PacketHashClass(packetClass, PacketFlow.SERVERBOUND, stage)
        );
    }

    static void registerCPacket(
            Class<? extends ClientboundPacket> packetClass,
            PacketStage stage
    ) {
        var id = switch (stage) {
            case HANDSHAKING -> CLIENTBOUND_ID.handshaking().getAndIncrement();
            case STATUS -> CLIENTBOUND_ID.status().getAndIncrement();
            case LOGIN -> CLIENTBOUND_ID.login().getAndIncrement();
            case CONFIGURATION -> CLIENTBOUND_ID.configuration().getAndIncrement();
            case PLAY -> CLIENTBOUND_ID.play().getAndIncrement();
        };
        CLASS_TO_ID.put(
                new PacketHashClass(packetClass, PacketFlow.CLIENTBOUND, stage),
                new PacketHashId(id, PacketFlow.CLIENTBOUND, stage)
        );
        ID_TO_CLASS.put(
                new PacketHashId(id, PacketFlow.CLIENTBOUND, stage),
                new PacketHashClass(packetClass, PacketFlow.CLIENTBOUND, stage)
        );
    }

    public static Class<? extends Packet> getPacketById(int packetId, PacketFlow packetFlow, PacketStage packetStage) {
        return ID_TO_CLASS.get(new PacketHashId(packetId, packetFlow, packetStage)).packetClass();
    }
}
