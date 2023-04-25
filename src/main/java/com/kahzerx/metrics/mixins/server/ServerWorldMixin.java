package com.kahzerx.metrics.mixins.server;

import com.kahzerx.metrics.helpers.ServerCollectorInterface;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerChunkManager.class)
public abstract class ServerWorldMixin {
    @Shadow public abstract World getWorld();

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;shouldTickBlocksInChunk(J)Z"))
    private boolean onTickChunk(ServerWorld instance, long chunkPos) {
        MinecraftServer server = instance.getServer();
        ((ServerCollectorInterface) server).getChunkProfiler().onChunkTick(this.getWorld().getRegistryKey().getValue().getPath());
        return this.getWorld().shouldTickBlocksInChunk(chunkPos);
    }
}
