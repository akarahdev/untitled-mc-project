import dev.akarah.protocol.handshaking.ServerboundHandshake;
import dev.akarah.protocol.login.ClientboundDisconnectLogin;
import dev.akarah.protocol.meta.PacketStage;
import dev.akarah.protocol.status.ClientboundStatusResponse;
import dev.akarah.protocol.status.ServerboundStatusRequest;
import dev.akarah.server.MinecraftServer;

public class TestServer {
    public static void main(String[] args) {
        var server = MinecraftServer.of(25565);
        server.eventManager().registerEvent(ServerboundHandshake.class, packetEvent -> {
            switch (packetEvent.packet().nextState()) {
                case 1 -> packetEvent.connection().stage(PacketStage.STATUS);
                case 2 -> packetEvent.connection().stage(PacketStage.LOGIN);
                case 3 -> packetEvent.connection().stage(PacketStage.TRANSFER);
            }
            if(packetEvent.packet().nextState() == 2)
                packetEvent.connection().send(new ClientboundDisconnectLogin("'ayo'"));
        });
        server.eventManager().registerEvent(ServerboundStatusRequest.class, packetEvent -> {
            packetEvent.connection().send(new ClientboundStatusResponse("""
                {
                    "version": {}
                }
                """));
        });
        server.start();
    }
}
