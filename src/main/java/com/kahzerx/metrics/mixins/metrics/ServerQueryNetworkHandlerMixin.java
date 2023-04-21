package com.kahzerx.metrics.mixins.metrics;

import com.kahzerx.metrics.helpers.IsStatPacketInterface;
import com.kahzerx.metrics.helpers.SetServerInterface;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerQueryNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerQueryNetworkHandler.class)
public class ServerQueryNetworkHandlerMixin implements IsStatPacketInterface {
    @Shadow @Final private MinecraftServer server;
    private boolean isMetrics = false;

    @Redirect(method = "onRequest", at = @At(value = "NEW", target = "net/minecraft/network/packet/s2c/query/QueryResponseS2CPacket"))
    private QueryResponseS2CPacket onReq(ServerMetadata metadata) {
        QueryResponseS2CPacket resp = new QueryResponseS2CPacket(metadata);
        ((IsStatPacketInterface) resp).setMetrics(this.isMetrics());
        ((SetServerInterface) resp).setServer(this.server);
        return resp;
    }

    @Override
    public void setMetrics(boolean stat) {
        this.isMetrics = stat;
    }

    @Override
    public boolean isMetrics() {
        return this.isMetrics;
    }
}
