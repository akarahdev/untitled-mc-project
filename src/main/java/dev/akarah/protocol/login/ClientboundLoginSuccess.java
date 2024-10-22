package dev.akarah.protocol.login;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.protocol.ServerboundPacket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ClientboundLoginSuccess(
        UUID uuid,
        String username,
        List<Property> properties,
        byte strictErrorHandling
) implements ClientboundPacket {
    public record Property(
            String name,
            String value,
            Optional<String> signature
    ) {
    }

    public static Format<ClientboundLoginSuccess> FORMAT = RecordFormat.ofRecord(
            RecordFormat.field(Format.uuid(), ClientboundLoginSuccess::uuid),
            RecordFormat.field(Format.string(), ClientboundLoginSuccess::username),
            RecordFormat.field(
                    Format.arrayOf(
                            RecordFormat.ofRecord(
                                    RecordFormat.field(Format.string(), Property::name),
                                    RecordFormat.field(Format.string(), Property::value),
                                    RecordFormat.field(
                                            Format.optionalOf(Format.string()),
                                            Property::signature
                                    ),
                                    Property::new
                            )
                    ),
                    ClientboundLoginSuccess::properties
            ),
            RecordFormat.field(Format.signedByte(), ClientboundLoginSuccess::strictErrorHandling),
            ClientboundLoginSuccess::new
    );
}
