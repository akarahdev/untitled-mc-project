import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacketId;
import dev.akarah.protocol.handshaking.ServerboundHandshake;
import dev.akarah.server.MinecraftServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class TestServer {
    public static void main(String[] args) {
        MinecraftServer.of(25565)
            .start();
    }
}
