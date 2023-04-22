package com.kahzerx.metrics.profiler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

public class TPSProfiler {
    private long lastTickTime = 0;
    private static final long SEC_IN_NANO = TimeUnit.SECONDS.toNanos(1);
    private static final int TPS = 20;
    private static final int TPS_SAMPLE_INTERVAL = 20;
    private static final BigDecimal TPS_BASE = new BigDecimal(SEC_IN_NANO).multiply(new BigDecimal(TPS_SAMPLE_INTERVAL));
    private final TpsRollingAverage tps5Sec = new TpsRollingAverage(5);
    private final TpsRollingAverage tps30Sec = new TpsRollingAverage(30);
    private final TpsRollingAverage tps1Min = new TpsRollingAverage(60);
    private final TpsRollingAverage[] tpsAverages = {this.tps5Sec, this.tps30Sec, this.tps1Min};

    public void onTick(int ticks) {
        if (ticks % TPS_SAMPLE_INTERVAL != 0) {
            return;
        }

        long now = System.nanoTime();

        if (this.lastTickTime == 0) {
            this.lastTickTime = now;
            return;
        }

        long diff = now - this.lastTickTime;
        BigDecimal currentTps = TPS_BASE.divide(new BigDecimal(diff), 30, RoundingMode.HALF_UP);
        BigDecimal total = currentTps.multiply(new BigDecimal(diff));

        for (TpsRollingAverage rollingAverage : this.tpsAverages) {
            rollingAverage.add(currentTps, diff, total);
        }

        this.lastTickTime = now;
    }

    public double tps5Sec() {
        return this.tps5Sec.getAverage();
    }

    public double tps30Sec() {
        return this.tps30Sec.getAverage();
    }

    public double tps1Min() {
        return this.tps1Min.getAverage();
    }

    public static final class TpsRollingAverage {
        private final int size;
        private long time;
        private BigDecimal total;
        private int index = 0;
        private final BigDecimal[] samples;
        private final long[] times;

        private TpsRollingAverage(int size) {
            this.size = size;
            this.time = size * SEC_IN_NANO;
            this.total = new BigDecimal(TPS).multiply(new BigDecimal(SEC_IN_NANO)).multiply(new BigDecimal(size));
            this.samples = new BigDecimal[size];
            this.times = new long[size];
            for (int i = 0; i < size; i++) {
                this.samples[i] = new BigDecimal(TPS);
                this.times[i] = SEC_IN_NANO;
            }
        }

        public void add(BigDecimal x, long t, BigDecimal total) {
            this.time -= this.times[this.index];
            this.total = this.total.subtract(this.samples[this.index].multiply(new BigDecimal(this.times[this.index])));
            this.samples[this.index] = x;
            this.times[this.index] = t;
            this.time += t;
            this.total = this.total.add(total);
            if (++this.index == this.size) {
                this.index = 0;
            }
        }

        public double getAverage() {
            return Math.min(this.total.divide(new BigDecimal(this.time), 1, RoundingMode.HALF_UP).doubleValue(), TPS);
        }
    }
}
