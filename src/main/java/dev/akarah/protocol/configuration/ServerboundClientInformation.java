package dev.akarah.protocol.configuration;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ServerboundPacket;

public record ServerboundClientInformation(
        String locale,
        byte viewDistance,
        ChatMode chatMode,
        byte displayedSkinParts,
        int mainHand,
        Filters filters
) implements ServerboundPacket {
    record ChatMode(
            int chatMode,
            byte chatColorsEnabled
    ) {}

    record Filters(
            byte enableTextFiltering,
            byte allowServerListings
    ) {}

    public static Format<ServerboundClientInformation> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(Format.string(), ServerboundClientInformation::locale),
        RecordFormat.field(Format.signedByte(), ServerboundClientInformation::viewDistance),
        RecordFormat.field(RecordFormat.ofRecord(
                RecordFormat.field(Format.varInt(), ChatMode::chatMode),
                RecordFormat.field(Format.signedByte(), ChatMode::chatColorsEnabled),
                ChatMode::new
        ), ServerboundClientInformation::chatMode),
            RecordFormat.field(Format.signedByte(), ServerboundClientInformation::displayedSkinParts),
        RecordFormat.field(Format.varInt(), ServerboundClientInformation::mainHand),
        RecordFormat.field(RecordFormat.ofRecord(
                RecordFormat.field(Format.signedByte(), Filters::enableTextFiltering),
                RecordFormat.field(Format.signedByte(), Filters::allowServerListings),
                Filters::new
        ), ServerboundClientInformation::filters),
        ServerboundClientInformation::new
    );
}
