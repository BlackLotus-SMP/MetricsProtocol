package com.kahzerx.metrics.helpers;

import com.kahzerx.metrics.profiler.TPSProfiler;

public interface ServerCollectorInterface {
    TPSProfiler getTPSProfiler();
    double getMSPT();
}
