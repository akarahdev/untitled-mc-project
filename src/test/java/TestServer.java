import dev.akarah.server.MinecraftServer;

public class TestServer {
    public static void main(String[] args) {
        MinecraftServer.of(25565)
            .start();
    }
}
