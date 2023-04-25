package com.kahzerx.metrics.mixins.server;

import com.kahzerx.metrics.helpers.ServerCollectorInterface;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerChunkManager.class, priority = 10)
public abstract class ServerWorldMixin {
    @Shadow public abstract World getWorld();

    @Inject(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V"))
    private void onTickChunk(CallbackInfo ci) {
        System.out.println("tick");
        MinecraftServer server = getWorld().getServer();
        if (server == null) {
            return;
        }
        ((ServerCollectorInterface) server).getChunkProfiler().onChunkTick(this.getWorld().getRegistryKey().getValue().getPath());
    }
}
