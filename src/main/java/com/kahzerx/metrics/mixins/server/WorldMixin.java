package com.kahzerx.metrics.mixins.server;

import com.kahzerx.metrics.helpers.ServerCollectorInterface;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(World.class)
public abstract class WorldMixin {
    @Inject(method = "tickEntity", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", shift = At.Shift.BEFORE))
    private <T extends Entity> void onTick(Consumer<T> tickConsumer, T entity, CallbackInfo ci) {
        MinecraftServer server = entity.getServer();
        if (server == null) {
            return;
        }
        ((ServerCollectorInterface) server).getEntityProfiler().onEntityTick(entity.getName().getString(), entity.getWorld().getRegistryKey().getValue().getPath());
    }
}
