package com.kahzerx.metrics;

import com.kahzerx.kahzerxmod.metrics.IsStatPacketInterface;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HandshakeC2SPacket.class)
public class HandshakeC2SPacketMixin implements IsStatPacketInterface {
    private boolean isMetric = false;
    @Redirect(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkState;byId(I)Lnet/minecraft/network/NetworkState;"))
    private NetworkState newHandshake(int id) {
        if (id == 9) {
            isMetric = true;
        }
        return NetworkState.byId(id);
    }

    @Override
    public boolean isMetrics() {
        return this.isMetric;
    }
}
