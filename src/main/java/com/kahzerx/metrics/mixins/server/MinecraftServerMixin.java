package com.kahzerx.metrics.mixins.server;

import com.kahzerx.metrics.helpers.ServerCollectorInterface;
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

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ServerCollectorInterface {
    @Shadow private int ticks;
    @Shadow @Final public long[] lastTickLengths;
    TPSProfiler tpsProfiler = new TPSProfiler();
    EntityProfiler entityProfiler = new EntityProfiler();
    private double mspt;
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/jfr/FlightProfiler;onTick(F)V"))
    private void onTick(CallbackInfo ci) {
        int ticks = this.ticks;
        this.tpsProfiler.onTick(ticks);
        this.entityProfiler.onTick();
        mspt = MathHelper.average(lastTickLengths) * 1.06E-6D;
    }

    @Override
    public TPSProfiler getTPSProfiler() {
        return this.tpsProfiler;
    }

    public EntityProfiler getEntityProfiler() {
        return entityProfiler;
    }

    @Override
    public double getMSPT() {
        return this.mspt;
    }
}
