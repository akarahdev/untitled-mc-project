package dev.akarah.protocol;

import dev.akarah.protocol.configuration.*;
import dev.akarah.protocol.handshaking.ServerboundHandshake;
import dev.akarah.protocol.login.ClientboundDisconnectLogin;
import dev.akarah.protocol.login.ClientboundLoginSuccess;
import dev.akarah.protocol.login.ServerboundLoginAcknowledged;
import dev.akarah.protocol.login.ServerboundLoginStart;
import dev.akarah.protocol.meta.*;
import dev.akarah.protocol.play.ClientboundLoginPlay;
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
            new AtomicInteger(0),
            new AtomicInteger(0)
        );
        CLIENTBOUND_ID = new AtomicPacketIdTracker(
            new AtomicInteger(0),
            new AtomicInteger(0),
            new AtomicInteger(0),
            new AtomicInteger(0),
            new AtomicInteger(0),
            new AtomicInteger(0)
        );

        ID_TO_CLASS = new ConcurrentHashMap<>();
        CLASS_TO_ID = new ConcurrentHashMap<>();

        registerSPacket(ServerboundHandshake.class, PacketStage.HANDSHAKING);

        registerSPacket(ServerboundStatusRequest.class, PacketStage.STATUS);
        registerSPacket(ServerboundPingRequest.class, PacketStage.STATUS);
        registerCPacket(ClientboundStatusResponse.class, PacketStage.STATUS);
        registerCPacket(ClientboundPongResponse.class, PacketStage.STATUS);


        registerCPacket(ClientboundDisconnectLogin.class, PacketStage.LOGIN);
        CLIENTBOUND_ID.login().set(0x02);
        registerCPacket(ClientboundLoginSuccess.class, PacketStage.LOGIN);

        registerSPacket(ServerboundLoginStart.class, PacketStage.LOGIN);
        SERVERBOUND_ID.login().set(0x03);
        registerSPacket(ServerboundLoginAcknowledged.class, PacketStage.LOGIN);

        registerCPacket(ClientboundCookieRequest.class, PacketStage.CONFIGURATION);
        registerCPacket(ClientboundPluginMessageConfiguration.class, PacketStage.CONFIGURATION);
        registerCPacket(ClientboundDisconnectConfiguration.class, PacketStage.CONFIGURATION);
        registerCPacket(ClientboundFinishConfiguration.class, PacketStage.CONFIGURATION);
        registerCPacket(ClientboundKeepAliveConfiguration.class, PacketStage.CONFIGURATION);
        registerCPacket(ClientboundPingConfiguration.class, PacketStage.CONFIGURATION);
        registerCPacket(ClientboundResetChat.class, PacketStage.CONFIGURATION);

        registerSPacket(ServerboundClientInformation.class, PacketStage.CONFIGURATION);
        registerSPacket(ServerboundCookieResponse.class, PacketStage.CONFIGURATION);
        registerSPacket(ServerboundPluginMessageConfiguration.class, PacketStage.CONFIGURATION);
        registerSPacket(ServerboundAcknowledgeFinishConfiguration.class, PacketStage.CONFIGURATION);
        registerSPacket(ServerboundKeepAliveConfiguration.class, PacketStage.CONFIGURATION);
        registerSPacket(ServerboundPongConfiguration.class, PacketStage.CONFIGURATION);
        registerSPacket(ServerboundResourcePackResponseConfiguration.class, PacketStage.CONFIGURATION);
        registerSPacket(ServerboundKnownPacks.class, PacketStage.CONFIGURATION);

        CLIENTBOUND_ID.play().set(0x2B);
        registerCPacket(ClientboundLoginPlay.class, PacketStage.PLAY);
    }

    static void registerPacket(
        Class<? extends Packet> packetClass,
        PacketFlow flow,
        PacketStage stage
    ) {
        var id = switch (flow) {
            case SERVERBOUND -> switch (stage) {
                case HANDSHAKING -> SERVERBOUND_ID.handshaking().getAndIncrement();
                case TRANSFER -> SERVERBOUND_ID.transfer().getAndIncrement();
                case STATUS -> SERVERBOUND_ID.status().getAndIncrement();
                case LOGIN -> SERVERBOUND_ID.login().getAndIncrement();
                case CONFIGURATION -> SERVERBOUND_ID.configuration().getAndIncrement();
                case PLAY -> SERVERBOUND_ID.play().getAndIncrement();
            };
            case CLIENTBOUND -> switch (stage) {
                case HANDSHAKING -> CLIENTBOUND_ID.handshaking().getAndIncrement();
                case TRANSFER -> CLIENTBOUND_ID.transfer().getAndIncrement();
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
            case TRANSFER -> SERVERBOUND_ID.transfer().getAndIncrement();
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
            case TRANSFER -> CLIENTBOUND_ID.transfer().getAndIncrement();
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
        var phi = new PacketHashId(packetId, packetFlow, packetStage);
        if(!ID_TO_CLASS.containsKey(phi))
            throw new NullPointerException("Unable to find packet for ID " + packetId + ", flow " + packetFlow + ", stage" + packetStage);
        return ID_TO_CLASS.get(phi).packetClass();
    }

    public static int getIdByPacket(Class<? extends Packet> packetClass, PacketFlow packetFlow, PacketStage packetStage) {
        return CLASS_TO_ID.get(new PacketHashClass(packetClass, packetFlow, packetStage)).packetId();
    }
}
