package com.kahzerx.metrics;

import com.kahzerx.kahzerxmod.metrics.IsStatPacketInterface;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.server.network.ServerQueryNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {

    @Shadow @Final private MinecraftServer server;

    @Shadow @Final private ClientConnection connection;

    @Inject(method = "onHandshake", at = @At(value = "HEAD"), cancellable = true)
    private void onHandshake(HandshakeC2SPacket packet, CallbackInfo ci) {
        if (((IsStatPacketInterface) packet).isMetrics()) {
            this.connection.setState(NetworkState.STATUS);
            ServerQueryNetworkHandler handler = new ServerQueryNetworkHandler(this.server, this.connection);
            ((IsStatPacketInterface) handler).setMetrics(((IsStatPacketInterface) packet).isMetrics());
            this.connection.setPacketListener(handler);
            ci.cancel();
        }
    }
}
