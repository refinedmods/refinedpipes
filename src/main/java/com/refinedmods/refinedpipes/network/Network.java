package com.refinedmods.refinedpipes.network;

import com.refinedmods.refinedpipes.network.graph.NetworkGraph;
import com.refinedmods.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.refinedmods.refinedpipes.network.pipe.Destination;
import com.refinedmods.refinedpipes.network.pipe.DestinationType;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public abstract class Network {
    protected final NetworkGraph graph = new NetworkGraph(this);
    private final String id;
    private BlockPos originPos;
    private boolean didDoInitialScan;

    public Network(BlockPos originPos, String id) {
        this.id = id;
        this.originPos = originPos;
    }

    public void setOriginPos(BlockPos originPos) {
        this.originPos = originPos;
    }

    public String getId() {
        return id;
    }

    public NetworkGraphScannerResult scanGraph(Level world, BlockPos pos) {
        return graph.scan(world, pos);
    }

    public List<Destination> getDestinations(DestinationType type) {
        return graph.getDestinations(type);
    }

    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putString("id", id);
        tag.putLong("origin", originPos.asLong());

        return tag;
    }

    public void update(Level world) {
        if (!didDoInitialScan) {
            didDoInitialScan = true;

            scanGraph(world, originPos);
        }

        graph.getPipes().forEach(Pipe::update);
    }

    public Pipe getPipe(BlockPos pos) {
        return graph.getPipes().stream().filter(p -> p.getPos().equals(pos)).findFirst().orElse(null);
    }

    public abstract void onMergedWith(Network mainNetwork);

    public abstract ResourceLocation getType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return id.equals(network.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
