package com.kahzerx.metrics.helpers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record Metrics(RAM ram, String version, Long day, Double mspt, String timeStarted, List<String> dimList, List<Player> playerList, TPS tps, List<ChunksPerDim> chunks, List<EntitiesPerDim> entities, List<BlockEntitiesPerDim> blockEntities) {
    public static final Codec<Metrics> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            RAM.CODEC.fieldOf("ram").forGetter(Metrics::ram),
            Codec.STRING.fieldOf("version").forGetter(Metrics::version),
            Codec.LONG.fieldOf("day").forGetter(Metrics::day),
            Codec.DOUBLE.fieldOf("mspt").forGetter(Metrics::mspt),
            Codec.STRING.fieldOf("time_started").forGetter(Metrics::timeStarted),
            Codec.STRING.listOf().fieldOf("dimensions").forGetter(Metrics::dimList),
            Player.CODEC.listOf().fieldOf("players").forGetter(Metrics::playerList),
            TPS.CODEC.fieldOf("tps").forGetter(Metrics::tps),
            ChunksPerDim.CODEC.listOf().fieldOf("chunks").forGetter(Metrics::chunks),
            EntitiesPerDim.CODEC.listOf().fieldOf("entities").forGetter(Metrics::entities),
            BlockEntitiesPerDim.CODEC.listOf().fieldOf("block_entities").forGetter(Metrics::blockEntities)
    ).apply(instance, Metrics::new));

    public record RAM(double used, double max) {
        public static final Codec<RAM> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.DOUBLE.fieldOf("used").forGetter(RAM::used),
                Codec.DOUBLE.fieldOf("max").forGetter(RAM::max)
        ).apply(instance, RAM::new));
    }

    public record Player(String playerName, String uuid, String dim, double posX, double posY, double posZ) {
        public static final Codec<Player> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(Player::playerName),
                Codec.STRING.fieldOf("uuid").forGetter(Player::uuid),
                Codec.STRING.fieldOf("dim").forGetter(Player::dim),
                Codec.DOUBLE.fieldOf("x").forGetter(Player::posX),
                Codec.DOUBLE.fieldOf("y").forGetter(Player::posY),
                Codec.DOUBLE.fieldOf("z").forGetter(Player::posZ)
        ).apply(instance, Player::new));
    }

    public record TPS(double tps5Sec, double tps30Sec, double tps1Min) {
        public static final Codec<TPS> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.DOUBLE.fieldOf("5s").forGetter(TPS::tps5Sec),
                Codec.DOUBLE.fieldOf("30s").forGetter(TPS::tps30Sec),
                Codec.DOUBLE.fieldOf("1m").forGetter(TPS::tps1Min)
        ).apply(instance, TPS::new));
    }

    public record ChunksPerDim(String dimName, int count) {
        public static final Codec<ChunksPerDim> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.fieldOf("dim").forGetter(ChunksPerDim::dimName),
                Codec.INT.fieldOf("count").forGetter(ChunksPerDim::count)
        ).apply(instance, ChunksPerDim::new));
    }

    public record EntitiesPerDim(String dimName, List<Count> entities) {
        public static final Codec<EntitiesPerDim> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.fieldOf("dim").forGetter(EntitiesPerDim::dimName),
                Count.CODEC.listOf().fieldOf("entities").forGetter(EntitiesPerDim::entities)
        ).apply(instance, EntitiesPerDim::new));
    }

    public record BlockEntitiesPerDim(String dimName, List<Count> blockEntities) {
        public static final Codec<BlockEntitiesPerDim> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.fieldOf("dim").forGetter(BlockEntitiesPerDim::dimName),
                Count.CODEC.listOf().fieldOf("block_entities").forGetter(BlockEntitiesPerDim::blockEntities)
        ).apply(instance, BlockEntitiesPerDim::new));
    }

    public record Count(String name, int count) {
        public static final Codec<Count> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(Count::name),
                Codec.INT.fieldOf("count").forGetter(Count::count)
        ).apply(instance, Count::new));
    }
}
