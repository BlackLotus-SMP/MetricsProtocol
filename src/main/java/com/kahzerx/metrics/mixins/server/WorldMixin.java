package com.kahzerx.metrics.mixins.server;

import com.kahzerx.metrics.helpers.ServerCollectorInterface;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "tickEntity", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", shift = At.Shift.BEFORE))
    private <T extends Entity> void onTick(Consumer<T> tickConsumer, T entity, CallbackInfo ci) {
        MinecraftServer server = entity.getServer();
        if (server == null) {
            return;
        }
        ((ServerCollectorInterface) server).getEntityProfiler().onEntityTick(entity.getName().getString(), entity.getWorld().getRegistryKey().getValue().getPath());
    }

    @Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;shouldTickBlockPos(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean onTickBE(World instance, BlockPos pos) {
        boolean shouldTick = instance.shouldTickBlockPos(pos);
        MinecraftServer server = instance.getServer();
        if (shouldTick && server != null) {
            BlockState be = instance.getBlockState(pos);
            ((ServerCollectorInterface) server).getBlockEntityProfiler().onBlockEntityTick(be.getBlock().getName().getString(), instance.getRegistryKey().getValue().getPath());
        }
        return shouldTick;
    }
}
