package com.kahzerx.metrics.mixins.server;

import com.kahzerx.metrics.helpers.ServerCollectorInterface;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {"net.minecraft.world.chunk.WorldChunk$DirectBlockEntityTickInvoker"})
public class WorldChunkMixin<T extends BlockEntity> {
    @Shadow @Final private T blockEntity;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntityTicker;tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BlockEntity;)V"))
    private void onTick(CallbackInfo ci) {
        World world = blockEntity.getWorld();
        if (world == null) {
            return;
        }
        MinecraftServer server = blockEntity.getWorld().getServer();
        if (server == null) {
            return;
        }
        ((ServerCollectorInterface) server).getBlockEntityProfiler().onBlockEntityTick(world.getBlockState(blockEntity.getPos()).getBlock().getName().getString(), world.getRegistryKey().getValue().getPath());
    }
}
