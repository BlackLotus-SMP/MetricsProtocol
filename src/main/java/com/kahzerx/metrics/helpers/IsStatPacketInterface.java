package com.kahzerx.metrics.helpers;

public interface IsStatPacketInterface {
    boolean isMetrics();
    default void setMetrics(boolean isMetrics) {}
}
