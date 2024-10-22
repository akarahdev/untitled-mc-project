import dev.akarah.protocol.configuration.*;
import dev.akarah.protocol.handshaking.ServerboundHandshake;
import dev.akarah.protocol.login.ClientboundDisconnectLogin;
import dev.akarah.protocol.login.ClientboundLoginSuccess;
import dev.akarah.protocol.login.ServerboundLoginAcknowledged;
import dev.akarah.protocol.login.ServerboundLoginStart;
import dev.akarah.protocol.meta.PacketStage;
import dev.akarah.protocol.status.ClientboundStatusResponse;
import dev.akarah.protocol.status.ServerboundStatusRequest;
import dev.akarah.server.MinecraftServer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestServer {
    public static void main(String[] args) {
        var server = MinecraftServer.of(25565);
        server.eventManager().registerEvent(ServerboundHandshake.class, packetEvent -> {
            switch (packetEvent.packet().nextState()) {
                case 1 -> packetEvent.connection().stage(PacketStage.STATUS);
                case 2 -> packetEvent.connection().stage(PacketStage.LOGIN);
                case 3 -> packetEvent.connection().stage(PacketStage.TRANSFER);
            }
        });
        server.eventManager().registerEvent(ServerboundStatusRequest.class, packetEvent -> {
            packetEvent.connection().send(new ClientboundStatusResponse("""
{"version": {"name": "1.21.1","protocol": "767"},"description": {"text": "Hello from the test server!"}}""".trim()));
        });

        server.eventManager().registerEvent(ServerboundLoginStart.class, packetEvent -> {
            packetEvent.connection().send(new ClientboundLoginSuccess(
                    packetEvent.packet().uuid(),
                    packetEvent.packet().username(),
                    List.of(),
                    (byte) 1
            ));
        });

        server.eventManager().registerEvent(ServerboundLoginAcknowledged.class, packetEvent -> {
            packetEvent.connection().stage(PacketStage.CONFIGURATION);
        });

        server.eventManager().registerEvent(ServerboundClientInformation.class, packetEvent -> {
            packetEvent.connection().send(new ClientboundFinishConfiguration());
        });

        server.eventManager().registerEvent(ServerboundAcknowledgeFinishConfiguration.class, packetEvent -> {
            packetEvent.connection().stage(PacketStage.PLAY);
        });
        server.start();
    }
}
