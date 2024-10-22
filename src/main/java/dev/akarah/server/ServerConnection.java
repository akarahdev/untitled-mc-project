package dev.akarah.server;

import dev.akarah.event.ServerboundPacketEvent;
import dev.akarah.network.Format;
import dev.akarah.network.PacketBuf;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.protocol.PacketIdentifiers;
import dev.akarah.protocol.ServerboundPacket;
import dev.akarah.protocol.meta.PacketFlow;
import dev.akarah.protocol.meta.PacketStage;
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
        System.out.println(this + " FULL R: " + r);
        return r;
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
            Format<ClientboundPacket> format = (Format<ClientboundPacket>) packet.format();
            var size = format.size(packet);
            var packetBuf = PacketBuf.allocate(
                format.size(packet) + Format.calculateVarIntSize(size)
            );
            packetBuf.writeVarInt(size + Format.calculateVarIntSize(size));
            format.write(packetBuf, packet);
            outputStream.write(packetBuf.toArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ApiUsage.Internal
    protected void socketLoop() throws IOException {
        while(true) {
            if(this.socket.isClosed())
                break;

            System.out.println("PACKET DATA TRACE @ " + this + " ----------");

            var packetLength = readVarIntFromStream();
            if(packetLength == -1)
                break;
            System.out.println(this + " Packet length: " + packetLength);

            var packetBuf = PacketBuf.allocate(packetLength);
            for(int i = 0; i < packetLength; i++) {
                var v = readByte();
                System.out.println(this + " Byte read: " + v);
                if(v == -1)
                    return;
                packetBuf.writeByte((byte) v);
            }


            System.out.println(this + " Packet buf: " + Arrays.toString(packetBuf.toArray()));

            var packetId = packetBuf.readVarInt();
            System.out.println(this + " Read packet id: " + packetId);

            var packetClass = PacketIdentifiers.getPacketById(packetId, PacketFlow.SERVERBOUND, this.stage);
            var rawField = ExceptionUtils.moveToRuntime(() -> packetClass.getField("FORMAT").get(null));
            var packetFormat = (Format<? extends ServerboundPacket>) rawField;
            System.out.println(this + " Packet class: " + packetClass.getName());
            var packetData = packetFormat.read(packetBuf);
            System.out.println(this + " Packet data: " + packetData);
            for(var event : minecraftServer.eventManager().serverboundPacketEvents) {
                try {
                    if(event.packetClass().isInstance(packetData))
                        event.packetHandler().runObject(new ServerboundPacketEvent<>(this, packetData));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("PACKET DATA TRACE END " + this.stage + " @ " + this + " ----------");

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
            var d = readByte();
            if(d == 1 && value == 0)
                return d;
            System.out.println("d = " + d);
            if(d == -1)
                return -1;
            currentByte = (byte) d;
            value |= (currentByte & 0x7F) << position;
            if ((currentByte & 0x8F) == 0) break;
            position += 7;
            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }
}
