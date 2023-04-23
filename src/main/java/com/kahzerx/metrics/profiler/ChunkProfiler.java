package com.kahzerx.metrics.profiler;

import java.util.HashMap;
import java.util.Map;

public class ChunkProfiler {
    Map<String, Integer> cachedChunks = new HashMap<>();
    Map<String, Integer> finalChunks = new HashMap<>();

    public void onEntityTick(String dimension) {
        this.cachedChunks.compute(dimension, (k, v) -> (v == null) ? 0 : v + 1);
    }

    public void onTick() {
        this.finalChunks = this.cachedChunks;
        this.cachedChunks = new HashMap<>();
    }

    public Map<String, Integer> getLoadedChunks() {
        return finalChunks;
    }
}
