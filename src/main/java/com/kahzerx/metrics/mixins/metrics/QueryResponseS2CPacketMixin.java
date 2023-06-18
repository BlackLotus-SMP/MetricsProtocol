package com.kahzerx.metrics.mixins.metrics;

import com.kahzerx.metrics.helpers.IsStatPacketInterface;
import com.kahzerx.metrics.helpers.Metrics;
import com.kahzerx.metrics.helpers.ServerCollectorInterface;
import com.kahzerx.metrics.helpers.SetServerInterface;
import com.kahzerx.metrics.profiler.BlockEntityProfiler;
import com.kahzerx.metrics.profiler.ChunkProfiler;
import com.kahzerx.metrics.profiler.EntityProfiler;
import com.kahzerx.metrics.profiler.TPSProfiler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(QueryResponseS2CPacket.class)
public class QueryResponseS2CPacketMixin implements IsStatPacketInterface, SetServerInterface {
    private boolean isMetrics = false;
    private MinecraftServer server;

    @Inject(method = "write", at = @At(value = "HEAD"), cancellable = true)
    private void onWrite(PacketByteBuf buf, CallbackInfo ci) {
        if (this.isMetrics()) {
            TPSProfiler tpsProf = ((ServerCollectorInterface) server).getTPSProfiler();
            Metrics.TPS tps = new Metrics.TPS(tpsProf.tps5Sec(), tpsProf.tps30Sec(), tpsProf.tps1Min());

            List<Metrics.EntitiesPerDim> entities = new ArrayList<>();
            EntityProfiler entityProf = ((ServerCollectorInterface) server).getEntityProfiler();
            for (Map.Entry<String, Map<String, Integer>> entitiesPerDim : entityProf.getTickingEntities().entrySet()) {
                List<Metrics.Count> counter = new ArrayList<>();
                for (Map.Entry<String, Integer> countEntities : entitiesPerDim.getValue().entrySet()) {
                    counter.add(new Metrics.Count(countEntities.getKey(), countEntities.getValue()));
                }
                entities.add(new Metrics.EntitiesPerDim(entitiesPerDim.getKey(), counter));
            }

            List<Metrics.BlockEntitiesPerDim> blockEntities = new ArrayList<>();
            BlockEntityProfiler blockEntityProf = ((ServerCollectorInterface) server).getBlockEntityProfiler();
            for (Map.Entry<String, Map<String, Integer>> blockEntitiesPerDim : blockEntityProf.getTickingBlockEntities().entrySet()) {
                List<Metrics.Count> counter = new ArrayList<>();
                for (Map.Entry<String, Integer> countEntities : blockEntitiesPerDim.getValue().entrySet()) {
                    counter.add(new Metrics.Count(countEntities.getKey(), countEntities.getValue()));
                }
                blockEntities.add(new Metrics.BlockEntitiesPerDim(blockEntitiesPerDim.getKey(), counter));
            }

            List<Metrics.ChunksPerDim> chunks = new ArrayList<>();
            ChunkProfiler chunkProf = ((ServerCollectorInterface) server).getChunkProfiler();
            for (Map.Entry<String, Integer> entry : chunkProf.getLoadedChunks().entrySet()) {
                chunks.add(new Metrics.ChunksPerDim(entry.getKey(), entry.getValue()));
            }

            List<ServerPlayerEntity> connectedPlayers = server.getPlayerManager().getPlayerList();
            List<Metrics.Player> players = new ArrayList<>();
            for (ServerPlayerEntity p : connectedPlayers) {
                players.add(new Metrics.Player(p.getName().getString(), p.getUuidAsString(), p.getWorld().getRegistryKey().getValue().getPath(), p.getX(), p.getY(), p.getZ()));
            }

            ArrayList<String> allDims = new ArrayList<>();
            for (ServerWorld world : this.server.getWorlds()) {
                allDims.add(world.getRegistryKey().getValue().getPath());
            }

            MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
            Metrics.RAM ram = new Metrics.RAM(
                    (double) mem.getHeapMemoryUsage().getUsed() / (1024 * 1024 * 1024),
                    (double) mem.getHeapMemoryUsage().getMax() / (1024 * 1024 * 1024)
            );
            Metrics m = new Metrics(
                    ram,
                    server.getVersion(),
                    (server.getOverworld().getTimeOfDay() / 24000),
                    ((ServerCollectorInterface) server).getMSPT(),
                    ((ServerCollectorInterface) server).getStartTime().toString(),
                    allDims,
                    players,
                    tps,
                    chunks,
                    entities,
                    blockEntities
            );
            buf.encodeAsJson(Metrics.CODEC, m);
            ci.cancel();
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
