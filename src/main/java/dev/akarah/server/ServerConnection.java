package dev.akarah.server;

import dev.akarah.event.ServerboundPacketEvent;
import dev.akarah.network.Format;
import dev.akarah.network.PacketBuf;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.protocol.PacketIdentifiers;
import dev.akarah.protocol.ServerboundPacket;
import dev.akarah.protocol.meta.PacketFlow;
import dev.akarah.protocol.meta.PacketStage;
import dev.akarah.registry.RegistryView;
import dev.akarah.types.ApiUsage;
import dev.akarah.util.ExceptionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class ServerConnection {
    Socket socket;
    PacketStage stage;
    MinecraftServer minecraftServer;

    InputStream inputStream;
    OutputStream outputStream;

    RegistryView registryView = new RegistryView();

    @ApiUsage.Internal
    protected ServerConnection(Socket socket, MinecraftServer minecraftServer) {
        this.socket = socket;
        this.stage = PacketStage.HANDSHAKING;
        this.minecraftServer = minecraftServer;

        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int readByte() throws IOException {
        var r = this.inputStream.read();
        return r;
    }

    public RegistryView registryView() {
        return this.registryView;
    }

    public void sendRegistryView() {
        this.registryView.sendToConnection(this);
    }

    public PacketStage stage() {
        return this.stage;
    }

    public void stage(PacketStage packetStage) {
        this.stage = packetStage;
    }

    public void send(ClientboundPacket packet) {
        if(this.socket.isClosed())
            return;
        try {
            var packetId = PacketIdentifiers.getIdByPacket(packet.getClass(), PacketFlow.CLIENTBOUND, this.stage);
            Format<ClientboundPacket> format = (Format<ClientboundPacket>) packet.format();

            var basePacketSize = format.size(packet);
            var packetIdSize = Format.calculateVarIntSize(packetId);
            var lengthOutput = basePacketSize + packetIdSize;

            var buf = PacketBuf.allocate(lengthOutput + Format.calculateVarIntSize(lengthOutput));
            buf.writeVarInt(lengthOutput);
            buf.writeVarInt(packetId);
            format.write(buf, packet);

            this.outputStream.write(buf.toArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //            var packetSize = format.size(packet);
//            var packetMetadataSize =
//                Format.calculateVarIntSize(packetSize + Format.calculateVarIntSize(packetSize))
//                + Format.calculateVarIntSize(id);
//            var totalSize = packetSize + packetMetadataSize;
//            System.out.println("Packet: " + packet);
//            System.out.println("Format: " + format);
//            System.out.println("Packet Size: " + packetSize);
//            var packetBuf = PacketBuf.allocate(totalSize);
//            System.out.println("Allocated size: " + packetBuf.toArray().length + " " + format.size(packet) + " " + totalSize);
//            System.out.println("Buf array #1: " + Arrays.toString(packetBuf.toArray()));
//            packetBuf.writeVarInt(totalSize);
//            packetBuf.writeVarInt(id);
//            System.out.println("Buf array #2: " + Arrays.toString(packetBuf.toArray()));
//            format.write(packetBuf, packet);
//            System.out.println("Buf array #3: " + Arrays.toString(packetBuf.toArray()));
//            for(var b : packetBuf.toArray())
//                outputStream.write(b);

    @ApiUsage.Internal
    protected void socketLoop() throws IOException {
        while(true) {
            if(this.socket.isClosed())
                break;

            var packetLength = readVarIntFromStream();
            if(packetLength == -1)
                break;

            var packetBuf = PacketBuf.allocate(packetLength);
            for(int i = 0; i < packetLength; i++) {
                var v = readByte();
                if(v == -1)
                    return;
                packetBuf.writeByte((byte) v);
            }


            var packetId = packetBuf.readVarInt();

            var packetClass = PacketIdentifiers.getPacketById(packetId, PacketFlow.SERVERBOUND, this.stage);
            var rawField = ExceptionUtils.moveToRuntime(() -> packetClass.getField("FORMAT").get(null));
            var packetFormat = (Format<? extends ServerboundPacket>) rawField;
            var packetData = packetFormat.read(packetBuf);
            for(var event : minecraftServer.eventManager().serverboundPacketEvents) {
                try {
                    if(event.packetClass().isInstance(packetData))
                        event.packetHandler().runObject(new ServerboundPacketEvent<>(this, packetData));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(this.socket.isClosed())
                break;
        }
    }

    @ApiUsage.Internal
    protected int readVarIntFromStream() throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            var rb = readByte();
            if(rb == -1) {
                throw new SocketException("socket ended?");
            }
            currentByte = (byte) rb;
            value |= (currentByte & PacketBuf.SEGMENT_BITS) << position;

            if ((currentByte & PacketBuf.CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }
}
