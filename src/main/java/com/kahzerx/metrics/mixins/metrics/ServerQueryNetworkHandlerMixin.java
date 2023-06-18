package com.kahzerx.metrics.mixins.metrics;

import com.kahzerx.metrics.helpers.IsStatPacketInterface;
import com.kahzerx.metrics.helpers.SetServerInterface;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerQueryNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerQueryNetworkHandler.class)
public class ServerQueryNetworkHandlerMixin implements IsStatPacketInterface, SetServerInterface {
    private boolean isMetrics = false;
    private MinecraftServer server = null;

    @Redirect(method = "onRequest", at = @At(value = "NEW", target = "(Lnet/minecraft/server/ServerMetadata;)Lnet/minecraft/network/packet/s2c/query/QueryResponseS2CPacket;"))
    private QueryResponseS2CPacket onReq(ServerMetadata metadata) {
        QueryResponseS2CPacket resp = new QueryResponseS2CPacket(metadata);
        ((IsStatPacketInterface) (Object) resp).setMetrics(this.isMetrics());
        ((SetServerInterface) (Object) resp).setServer(this.getServer());
        return resp;
    }

    private MinecraftServer getServer() {
        return this.server;
    }

    @Override
    public void setMetrics(boolean stat) {
        this.isMetrics = stat;
    }

    @Override
    public boolean isMetrics() {
        return this.isMetrics;
    }

    @Override
    public void setServer(MinecraftServer server) {
        this.server = server;
    }
}
