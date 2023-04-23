package com.kahzerx.metrics.helpers;

import com.kahzerx.metrics.profiler.BlockEntityProfiler;
import com.kahzerx.metrics.profiler.EntityProfiler;
import com.kahzerx.metrics.profiler.TPSProfiler;

public interface ServerCollectorInterface {
    TPSProfiler getTPSProfiler();
    EntityProfiler getEntityProfiler();
    BlockEntityProfiler getBlockEntityProfiler();
    double getMSPT();
}
