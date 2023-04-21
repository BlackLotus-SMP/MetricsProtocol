package com.kahzerx.metrics;

import com.kahzerx.kahzerxmod.profiler.TPSProfiler;

public interface ServerCollectorInterface {
    TPSProfiler getTPSProfiler();
    double getMSPT();
}
