package dev.akarah.protocol.handshaking;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.PacketIdentifiers;
import dev.akarah.protocol.ServerboundPacket;

public record ServerboundHandshake(
    int protocolVersion,
    String serverAddress,
    int serverPort,
    int nextState
) implements ServerboundPacket {
    public static Format<ServerboundHandshake> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.varInt(), ServerboundHandshake::protocolVersion),
        RecordFormat.field(Format.string(), ServerboundHandshake::serverAddress),
        RecordFormat.field(Format.unsignedShort(), ServerboundHandshake::serverPort),
        RecordFormat.field(Format.varInt(), ServerboundHandshake::nextState),
        ServerboundHandshake::new
    );
}
