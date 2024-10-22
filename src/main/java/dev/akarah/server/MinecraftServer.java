package dev.akarah.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MinecraftServer {
    int port;
    ServerSocket serverSocket;
    EventManager eventManager;

    public static MinecraftServer of(int port) {
        var mc = new MinecraftServer();
        mc.port = port;
        mc.eventManager = new EventManager();
        return mc;
    }

    public EventManager eventManager() {
        return this.eventManager;
    }

    public void start() {
        try {
            this.serverSocket = new ServerSocket(port);
            while(true) {
                var newConnection = serverSocket.accept();
                Thread.ofPlatform().name("ConnectionHandler-" + newConnection).start(() -> {
                    try {
                        handleConnection(newConnection);
                    } catch (IOException | ArrayIndexOutOfBoundsException e) {
                        System.out.println("Something may have went wrong. Printing stack trace for debugging.");
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnection(Socket socket) throws IOException {
        var connection = new ServerConnection(socket, this);
        connection.socketLoop();
    }
}
