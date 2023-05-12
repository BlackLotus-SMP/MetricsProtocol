package com.kahzerx.metrics.helpers;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record Metrics(TPS tps, MSPT mspt, Players players, Version version, RAM ram, Entities entities, BlockEntities blockEntities, Chunks chunks, Dimensions dimensions, Uptime uptime, Day day) {
    public static class Codec implements JsonSerializer<Metrics> {
        @Override
        public JsonElement serialize(Metrics metrics, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("version", metrics.version().version());
            jsonObject.addProperty("mspt", metrics.mspt().mspt());
            jsonObject.addProperty("time_started", metrics.uptime().startTime());
            jsonObject.addProperty("day", metrics.day().day());
            jsonObject.add("tps", jsonSerializationContext.serialize(metrics.tps()));
            jsonObject.add("players", jsonSerializationContext.serialize(metrics.players()));
            jsonObject.add("ram", jsonSerializationContext.serialize(metrics.ram()));
            jsonObject.add("entities", jsonSerializationContext.serialize(metrics.entities()));
            jsonObject.add("block_entities", jsonSerializationContext.serialize(metrics.blockEntities()));
            jsonObject.add("chunks", jsonSerializationContext.serialize(metrics.chunks()));
            jsonObject.add("dimensions", jsonSerializationContext.serialize(metrics.dimensions()));
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

    public record TPS(double tps5Sec, double tps30Sec, double tps1Min) {
        public static class Codec implements JsonSerializer<TPS> {
            @Override
            public JsonElement serialize(TPS tps, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("5s", tps.tps5Sec());
                jsonObject.addProperty("30s", tps.tps30Sec());
                jsonObject.addProperty("1m", tps.tps1Min());
                return jsonObject;
            }
        }
    }

    public record Entities(Map<String, Map<String, Integer>> entityProf) {
        public static class Codec implements JsonSerializer<Entities> {
            @Override
            public JsonElement serialize(Entities entities, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonArray dimArray = new JsonArray();
                for (Map.Entry<String, Map<String, Integer>> dim : entities.entityProf().entrySet()) {
                    JsonObject dimObj = new JsonObject();
                    dimObj.addProperty("dim", dim.getKey());
                    dimObj.add("entities", extractFromDim(dim.getValue().entrySet()));
                    dimArray.add(dimObj);
                }
                return dimArray;
            }
        }
    }

    public record BlockEntities(Map<String, Map<String, Integer>> blockEntityProf) {
        public static class Codec implements JsonSerializer<BlockEntities> {
            @Override
            public JsonElement serialize(BlockEntities blockEntities, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonArray dimArray = new JsonArray();
                for (Map.Entry<String, Map<String, Integer>> dim : blockEntities.blockEntityProf().entrySet()) {
                    JsonObject dimObj = new JsonObject();
                    dimObj.addProperty("dim", dim.getKey());
                    dimObj.add("block_entities", extractFromDim(dim.getValue().entrySet()));
                    dimArray.add(dimObj);
                }
                return dimArray;
            }
        }
    }

    public record Chunks(Map<String, Integer> chunksProf) {
        public static class Codec implements JsonSerializer<Chunks> {
            @Override
            public JsonElement serialize(Chunks chunks, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonArray dimArray = new JsonArray();
                for (Map.Entry<String, Integer> dim : chunks.chunksProf().entrySet()) {
                    JsonObject dimObj = new JsonObject();
                    dimObj.addProperty("dim", dim.getKey());
                    dimObj.addProperty("count", dim.getValue());
                    dimArray.add(dimObj);
                }
                return dimArray;
            }
        }
    }

    private static JsonArray extractFromDim(Set<Map.Entry<String, Integer>> entries) {
        JsonArray array = new JsonArray();
        for (Map.Entry<String, Integer> data : entries) {
            JsonObject blockEntityObject = new JsonObject();
            blockEntityObject.addProperty("name", data.getKey());
            blockEntityObject.addProperty("count", data.getValue());
            array.add(blockEntityObject);
        }
        return array;
    }

    public record MSPT(double mspt) {}

    public record Version(String version) {}

    public record Uptime(String startTime) {}

    public record Day(Long day) {}

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

    public record Dimensions(ArrayList<String> dimList) {
        public static class Codec implements JsonSerializer<Dimensions> {
            @Override
            public JsonElement serialize(Dimensions dimensions, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonArray array = new JsonArray();
                for (String dimName : dimensions.dimList()) {
                    array.add(dimName);
                }
                return array;
            }
        }
    }
}
