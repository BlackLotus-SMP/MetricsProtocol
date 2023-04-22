package com.kahzerx.metrics.profiler;

import java.util.HashMap;
import java.util.Map;

public class EntityProfiler {
    Map<String,Map<String,Integer>> cachedEntities = new HashMap<>();
    Map<String,Map<String,Integer>> finalEntities = new HashMap<>();
    public void onEntityTick(String entityName, String dimension) {
        this.cachedEntities.compute(dimension, (k, v) -> (v == null) ? new HashMap<>() : v);
        this.cachedEntities.get(dimension).compute(entityName, (k, v) -> (v == null) ? 1 : v + 1);
    }
    public void onTick() {
        this.finalEntities = this.cachedEntities;
        this.cachedEntities = new HashMap<>();
    }

    public Map<String, Map<String, Integer>> getTickingEntities() {
        return finalEntities;
    }
}
