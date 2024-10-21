package dev.akarah.server;

import dev.akarah.network.Format;
import dev.akarah.network.PacketBuf;
import dev.akarah.protocol.PacketIdentifiers;
import dev.akarah.protocol.ServerboundPacket;
import dev.akarah.protocol.meta.PacketFlow;
import dev.akarah.protocol.meta.PacketStage;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class MinecraftServer {
    int port;
    ServerSocket serverSocket;

    public static MinecraftServer of(int port) {
        var mc = new MinecraftServer();
        mc.port = port;
        return mc;
    }

    public void start() {
        try {
            this.serverSocket = new ServerSocket(port);
            while(true) {
                var newConnection = serverSocket.accept();
                Thread.ofVirtual().start(() -> {
                    try {
                        handleConnection(newConnection);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnection(Socket socket) throws IOException {
        try {
            while(true) {
                var packetLength = readVarIntFromStream(socket.getInputStream());
                var packetBuf = PacketBuf.allocate(packetLength);
                for(var v : socket.getInputStream().readNBytes(packetLength))
                    packetBuf.writeByte(v);
                var packetId = packetBuf.readVarInt();

                var packetClass = PacketIdentifiers.getPacketById(packetId, PacketFlow.SERVERBOUND, PacketStage.HANDSHAKING);
                var packetFormat = (Format<? extends ServerboundPacket>) packetClass.getField("FORMAT").get(null);
                var packetData = packetFormat.read(packetBuf);
                System.out.println("Packet length: " + packetLength);
                System.out.println("Packet id: " + packetId);
                System.out.println("Packet format: " + packetFormat);
                System.out.println("Packet data: " + packetData);
            }
        } catch (SocketException e) {

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private int readVarIntFromStream(InputStream inputStream) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            var d = inputStream.read();
            if(d == -1)
                throw new SocketException("socket is closed");
            currentByte = (byte) d;
            value |= (currentByte & 0x7F) << position;

            if ((currentByte & 0x8F) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }
}
