package dev.akarah.protocol;

import dev.akarah.protocol.handshaking.ServerboundHandshake;
import dev.akarah.protocol.status.ClientboundPongResponse;
import dev.akarah.protocol.status.ClientboundStatusResponse;
import dev.akarah.protocol.status.ServerboundPingRequest;
import dev.akarah.protocol.status.ServerboundStatusRequest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientboundPacketId {
    private static final AtomicInteger HANDSHAKING_ID = new AtomicInteger(0);
    private static final AtomicInteger STATUS_ID = new AtomicInteger(0);
    private static final AtomicInteger LOGIN_ID = new AtomicInteger(0);
    private static final AtomicInteger CONFIGURATION_ID = new AtomicInteger(0);
    private static final AtomicInteger PLAY_ID = new AtomicInteger(0);

    static {
        PACKET_IDS = new ConcurrentHashMap<>();
        INVERSE_PACKET_IDS = new ConcurrentHashMap<>();

        registerPacket(STATUS_ID, ClientboundStatusResponse.class);
        registerPacket(STATUS_ID, ClientboundPongResponse.class);
    }

    static ConcurrentHashMap<Integer, String> PACKET_IDS;
    static ConcurrentHashMap<String, Integer> INVERSE_PACKET_IDS;

    public static void registerPacket(AtomicInteger id, Class<? extends ClientboundPacket> packet) {
        PACKET_IDS.put(id.getAndIncrement(), packet.getName());
        INVERSE_PACKET_IDS.put(packet.getName(), id.get()-1);
    }

    public static int packetId(Class<? extends ClientboundPacket> clazz) {
        return INVERSE_PACKET_IDS.get(clazz.getName());
    }
}
