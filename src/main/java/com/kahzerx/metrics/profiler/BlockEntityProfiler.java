package com.kahzerx.metrics.profiler;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityProfiler {
    Map<String, Map<String, Integer>> cachedBlockEntities = new HashMap<>();
    Map<String, Map<String, Integer>> finalBlockEntities = new HashMap<>();

    public void onBlockEntityTick(String blockEntityName, String dimension) {
        this.cachedBlockEntities.compute(dimension, (k, v) -> (v == null) ? new HashMap<>() : v);
        this.cachedBlockEntities.get(dimension).compute(blockEntityName, (k, v) -> (v == null) ? 1 : v + 1);
    }

    public void onTick() {
        this.finalBlockEntities = this.cachedBlockEntities;
        this.cachedBlockEntities = new HashMap<>();
    }

    public Map<String, Map<String, Integer>> getTickingBlockEntities() {
        return finalBlockEntities;
    }
}
