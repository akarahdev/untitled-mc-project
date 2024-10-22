package dev.akarah.protocol.play;

import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;
import dev.akarah.protocol.ClientboundPacket;
import dev.akarah.types.BlockPos;

import java.util.List;
import java.util.Optional;

public record ClientboundLoginPlay(
    PlayerData playerData,
    List<String> dimensions,
    ServerRestrictions serverRestrictions,
    DimensionData dimensionData,
    Optional<DeathLocation> deathLocation,
    int portalCooldown,
    boolean enforcesSecureChat
) implements ClientboundPacket {
    public record PlayerData(
        int entityId,
        boolean isHardcore
    ) {
        public static Format<PlayerData> FORMAT = RecordFormat.ofRecord(
            RecordFormat.field(Format.signedInt(), PlayerData::entityId),
            RecordFormat.field(Format.bool(), PlayerData::isHardcore),
            PlayerData::new
        );
    }

    public record ServerRestrictions(
        int maxPlayers,
        int viewDistance,
        int simulationDistance,
        boolean reducedDebugInfo,
        boolean enableRespawnScreen,
        boolean doLimitedCrafting
    ) {
        public static Format<ServerRestrictions> FORMAT = RecordFormat.ofRecord(
            RecordFormat.field(Format.varInt(), ServerRestrictions::maxPlayers),
            RecordFormat.field(Format.varInt(), ServerRestrictions::viewDistance),
            RecordFormat.field(Format.varInt(), ServerRestrictions::simulationDistance),
            RecordFormat.field(Format.bool(), ServerRestrictions::reducedDebugInfo),
            RecordFormat.field(Format.bool(), ServerRestrictions::enableRespawnScreen),
            RecordFormat.field(Format.bool(), ServerRestrictions::doLimitedCrafting),
            ServerRestrictions::new
        );
    }

    public record DimensionData(
        int dimensionType,
        String dimensionName,
        long hashedSeed,
        byte gameMode,
        byte previousGameMode,
        boolean isDebug,
        boolean isFlat
    ) {
        public static Format<DimensionData> FORMAT = RecordFormat.ofRecord(
            RecordFormat.field(Format.varInt(), DimensionData::dimensionType),
            RecordFormat.field(Format.string(), DimensionData::dimensionName),
            RecordFormat.field(Format.signedLong(), DimensionData::hashedSeed),
            RecordFormat.field(Format.signedByte(), DimensionData::gameMode),
            RecordFormat.field(Format.signedByte(), DimensionData::previousGameMode),
            RecordFormat.field(Format.bool(), DimensionData::isDebug),
            RecordFormat.field(Format.bool(), DimensionData::isFlat),
            DimensionData::new
        );
    }

    public record DeathLocation(
        String dimensionName,
        BlockPos position
    ) {
        public static Format<DeathLocation> FORMAT = RecordFormat.ofRecord(
            RecordFormat.field(Format.string(), DeathLocation::dimensionName),
            RecordFormat.field(Format.blockPos(), DeathLocation::position),
            DeathLocation::new
        );
    }


    // TODO: add dimension type registry
    public static Format<ClientboundLoginPlay> FORMAT = RecordFormat.ofRecord(
        RecordFormat.field(PlayerData.FORMAT, ClientboundLoginPlay::playerData),
        RecordFormat.field(Format.arrayOf(Format.string()), ClientboundLoginPlay::dimensions),
        RecordFormat.field(ServerRestrictions.FORMAT, ClientboundLoginPlay::serverRestrictions),
        RecordFormat.field(DimensionData.FORMAT, ClientboundLoginPlay::dimensionData),
        RecordFormat.field(Format.optionalOf(DeathLocation.FORMAT), ClientboundLoginPlay::deathLocation),
        RecordFormat.field(Format.varInt(), ClientboundLoginPlay::portalCooldown),
        RecordFormat.field(Format.bool(), ClientboundLoginPlay::enforcesSecureChat),
        ClientboundLoginPlay::new
    );
}
