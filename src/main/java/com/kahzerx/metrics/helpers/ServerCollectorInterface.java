package com.kahzerx.metrics.helpers;

import com.kahzerx.metrics.profiler.BlockEntityProfiler;
import com.kahzerx.metrics.profiler.ChunkProfiler;
import com.kahzerx.metrics.profiler.EntityProfiler;
import com.kahzerx.metrics.profiler.TPSProfiler;

import java.time.ZonedDateTime;

public interface ServerCollectorInterface {
    TPSProfiler getTPSProfiler();
    EntityProfiler getEntityProfiler();
    BlockEntityProfiler getBlockEntityProfiler();
    ChunkProfiler getChunkProfiler();
    double getMSPT();
    ZonedDateTime getStartTime();
}
