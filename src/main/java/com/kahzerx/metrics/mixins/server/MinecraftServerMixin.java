package com.kahzerx.metrics.mixins.server;

import com.kahzerx.kahzerxmod.metrics.ServerCollectorInterface;
import com.kahzerx.kahzerxmod.profiler.TPSProfiler;
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
    private double mspt;
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/jfr/FlightProfiler;onTick(F)V"))
    private void onTick(CallbackInfo ci) {
        int ticks = this.ticks;
        this.tpsProfiler.onTick(ticks);
        mspt = MathHelper.average(lastTickLengths) * 1.06E-6D;
    }

    @Override
    public TPSProfiler getTPSProfiler() {
        return this.tpsProfiler;
    }

    @Override
    public double getMSPT() {
        return this.mspt;
    }
}
