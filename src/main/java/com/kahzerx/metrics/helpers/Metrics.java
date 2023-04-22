package com.kahzerx.metrics.helpers;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class Metrics {
    private final TPS tps;
    private final MSPT mspt;
    private final Players players;
    private final Version version;
    private final RAM ram;

    public Metrics(TPS tps, MSPT mspt, Players players, Version version, RAM ram) {
        this.tps = tps;
        this.mspt = mspt;
        this.players = players;
        this.version = version;
        this.ram = ram;
    }

    public Players getPlayers() {
        return players;
    }

    public TPS getTps() {
        return tps;
    }

    public MSPT getMspt() {
        return mspt;
    }

    public Version getVersion() {
        return version;
    }

    public RAM getRam() {
        return ram;
    }

    public static class Codec implements JsonSerializer<Metrics> {
        @Override
        public JsonElement serialize(Metrics metrics, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("version", metrics.getVersion().version());
            jsonObject.addProperty("mspt", metrics.getMspt().mspt());
            jsonObject.add("tps", jsonSerializationContext.serialize(metrics.getTps()));
            jsonObject.add("players", jsonSerializationContext.serialize(metrics.getPlayers()));
            jsonObject.add("ram", jsonSerializationContext.serialize(metrics.getRam()));
            return jsonObject;
        }
    }

    public record Player(String playerName, String uuid, String dim, double posX, double posY, double posZ) {}

    public record Players(List<Player> playerList) {
        public static class Codec implements JsonSerializer<Players> {
            @Override
            public JsonElement serialize(Players players, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonArray jsonArray = new JsonArray();
                for (Player p : players.playerList()) {
                    JsonObject playerObject = new JsonObject();
                    playerObject.addProperty("name", p.playerName());
                    playerObject.addProperty("uuid", p.uuid());
                    playerObject.addProperty("dim", p.dim());
                    playerObject.addProperty("x", p.posX());
                    playerObject.addProperty("y", p.posY());
                    playerObject.addProperty("z", p.posZ());
                    jsonArray.add(playerObject);
                }
                return jsonArray;
            }
        }
    }

    public static class TPS {
        private final double tps5Sec;
        private final double tps30Sec;
        private final double tps1Min;
        public TPS(double tps5Sec, double tps30Sec, double tps1Min) {
            this.tps5Sec = tps5Sec;
            this.tps30Sec = tps30Sec;
            this.tps1Min = tps1Min;
        }

        public static class Codec implements JsonSerializer<TPS> {
            @Override
            public JsonElement serialize(TPS tps, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("5s", tps.tps5Sec);
                jsonObject.addProperty("30s", tps.tps30Sec);
                jsonObject.addProperty("1m", tps.tps1Min);
                return jsonObject;
            }
        }
    }

    public record MSPT(double mspt) {}

    public record Version(String version) {}

    public record RAM(double used, double max) {
        public static class Codec implements JsonSerializer<RAM> {
            @Override
            public JsonElement serialize(RAM ram, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("used", ram.used());
                jsonObject.addProperty("max", ram.max());
                return jsonObject;
            }
        }
    }
}
