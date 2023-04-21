package com.kahzerx.metrics;

public interface IsStatPacketInterface {
    boolean isMetrics();
    default void setMetrics(boolean isMetrics) {}
}
