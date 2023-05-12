package com.kahzerx.metrics.mixins.server;

import com.kahzerx.metrics.helpers.ServerCollectorInterface;
import com.kahzerx.metrics.profiler.BlockEntityProfiler;
import com.kahzerx.metrics.profiler.ChunkProfiler;
import com.kahzerx.metrics.profiler.EntityProfiler;
import com.kahzerx.metrics.profiler.TPSProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.ZonedDateTime;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ServerCollectorInterface {
    @Shadow private int ticks;
    @Shadow @Final public long[] lastTickLengths;
    private final TPSProfiler tpsProfiler = new TPSProfiler();
    private final EntityProfiler entityProfiler = new EntityProfiler();
    private final BlockEntityProfiler blockEntityProfiler = new BlockEntityProfiler();
    private final ChunkProfiler chunkProfiler = new ChunkProfiler();
    private double mspt;
    private final ZonedDateTime startTime = ZonedDateTime.now();

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/jfr/FlightProfiler;onTick(F)V"))
    private void onTick(CallbackInfo ci) {
        int ticks = this.ticks;
        this.tpsProfiler.onTick(ticks);
        this.entityProfiler.onTick();
        this.blockEntityProfiler.onTick();
        this.chunkProfiler.onTick();
        mspt = MathHelper.average(lastTickLengths) * 1.06E-6D;
    }

    @Override
    public TPSProfiler getTPSProfiler() {
        return this.tpsProfiler;
    }

    public EntityProfiler getEntityProfiler() {
        return this.entityProfiler;
    }

    public BlockEntityProfiler getBlockEntityProfiler() {
        return this.blockEntityProfiler;
    }

    public ChunkProfiler getChunkProfiler() {
        return chunkProfiler;
    }

    @Override
    public double getMSPT() {
        return this.mspt;
    }

    public ZonedDateTime getStartTime() {
        return this.startTime;
    }
}
