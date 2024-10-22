package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

import java.util.List;
import java.util.Optional;

public record ServerboundCookieResponse(
        String identifier,
        Optional<List<Byte>> payload
) implements ServerboundPacket {

    public static Format<ServerboundCookieResponse> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.string(), ServerboundCookieResponse::identifier),
        RecordFormat.field(
                Format.optionalOf(Format.arrayOf(Format.signedByte())),
                ServerboundCookieResponse::payload),
        ServerboundCookieResponse::new
    );
}
