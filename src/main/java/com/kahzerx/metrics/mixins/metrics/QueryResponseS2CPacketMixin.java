package com.kahzerx.metrics.mixins.metrics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kahzerx.metrics.helpers.IsStatPacketInterface;
import com.kahzerx.metrics.helpers.Metrics;
import com.kahzerx.metrics.helpers.ServerCollectorInterface;
import com.kahzerx.metrics.helpers.SetServerInterface;
import com.kahzerx.metrics.profiler.EntityProfiler;
import com.kahzerx.metrics.profiler.TPSProfiler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;

@Mixin(QueryResponseS2CPacket.class)
public class QueryResponseS2CPacketMixin implements IsStatPacketInterface, SetServerInterface {
    private final Gson metricsParse = new GsonBuilder().
            registerTypeAdapter(Metrics.class, new Metrics.Codec()).
            registerTypeAdapter(Metrics.TPS.class, new Metrics.TPS.Codec()).
            registerTypeAdapter(Metrics.Players.class, new Metrics.Players.Codec()).
            registerTypeAdapter(Metrics.RAM.class, new Metrics.RAM.Codec()).
            registerTypeAdapter(Metrics.Entities.class, new Metrics.Entities.Codec()).
            create();
    private boolean isMetrics = false;
    private MinecraftServer server;

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeString(Ljava/lang/String;)Lnet/minecraft/network/PacketByteBuf;"))
    private PacketByteBuf onWrite(PacketByteBuf instance, String string) {
        if (this.isMetrics()) {
            TPSProfiler tpsProf = ((ServerCollectorInterface) server).getTPSProfiler();
            EntityProfiler entityProf = ((ServerCollectorInterface) server).getEntityProfiler();
            Metrics.TPS tps = new Metrics.TPS(tpsProf.tps5Sec(), tpsProf.tps30Sec(), tpsProf.tps1Min());
            Metrics.Entities entities = new Metrics.Entities(entityProf.getTickingEntities());
            Metrics.MSPT mspt = new Metrics.MSPT(((ServerCollectorInterface) server).getMSPT());
            List<ServerPlayerEntity> connectedPlayers = server.getPlayerManager().getPlayerList();
            List<Metrics.Player> playerMetricList = new ArrayList<>();
            for (ServerPlayerEntity p : connectedPlayers) {
                playerMetricList.add(new Metrics.Player(
                        p.getName().getString(),
                        p.getUuidAsString(),
                        p.getWorld().getRegistryKey().getValue().getPath(),
                        p.getX(),
                        p.getY(),
                        p.getZ()
                ));
            }
            Metrics.Players players = new Metrics.Players(playerMetricList);
            Metrics.Version version = new Metrics.Version(server.getVersion());
            MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
            Metrics.RAM ram = new Metrics.RAM(
                    (double) mem.getHeapMemoryUsage().getUsed() / (1024 * 1024 * 1024),
                    (double) mem.getHeapMemoryUsage().getMax() / (1024 * 1024 * 1024)
            );
            Metrics m = new Metrics(tps, mspt, players, version, ram, entities);
            return instance.writeString(metricsParse.toJson(m), Short.MAX_VALUE);  // TODO probably make this Integer
        } else {
            return instance.writeString(string);
        }
    }

    @Override
    public boolean isMetrics() {
        return this.isMetrics;
    }

    @Override
    public void setMetrics(boolean stat) {
        this.isMetrics = stat;
    }

    @Override
    public void setServer(MinecraftServer server) {
        this.server = server;
    }
}
