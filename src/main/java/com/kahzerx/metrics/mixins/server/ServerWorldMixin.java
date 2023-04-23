package com.kahzerx.metrics.mixins.server;

import com.kahzerx.metrics.helpers.ServerCollectorInterface;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "tickChunk", at = @At(value = "HEAD"))
    private void onTickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        MinecraftServer server = chunk.getWorld().getServer();
        if (server == null) {
            return;
        }
        ((ServerCollectorInterface) server).getChunkProfiler().onEntityTick(chunk.getWorld().getRegistryKey().getValue().getPath());
    }
}
