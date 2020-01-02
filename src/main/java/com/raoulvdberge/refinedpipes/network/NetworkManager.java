package com.raoulvdberge.refinedpipes.network;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScanner;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class NetworkManager extends WorldSavedData {
    private static final String NAME = RefinedPipes.ID + "_networks";
    private static final Logger LOGGER = LogManager.getLogger(NetworkManager.class);

    public static NetworkManager get(World world) {
        return get((ServerWorld) world);
    }

    public static NetworkManager get(ServerWorld world) {
        String name = NAME + "_" + world.getDimension().getType().getRegistryName().getNamespace() + "_" + world.getDimension().getType().getRegistryName().getPath();

        return world.getSavedData().getOrCreate(() -> new NetworkManager(name, world), name);
    }

    private final World world;
    private final Set<Network> networks = new HashSet<>();
    private final Map<BlockPos, Pipe> pipes = new HashMap<>();

    public NetworkManager(String name, World world) {
        super(name);

        this.world = world;
    }

    public void addNetwork(Network network) {
        if (!networks.add(network)) {
            throw new RuntimeException("Duplicate network " + network.getId());
        }

        LOGGER.debug("Network {} created", network.getId());

        markDirty();
    }

    public void removeNetwork(Network network) {
        if (!networks.remove(network)) {
            throw new RuntimeException("Network " + network.getId() + " not found");
        }

        LOGGER.debug("Network {} removed", network.getId());

        markDirty();
    }

    private void formNetworkAt(World world, BlockPos pos) {
        Network network = new Network();

        addNetwork(network);

        network.scanGraph(world, pos);
    }

    private void mergeNetworksIntoOneAt(Set<Pipe> candidates, World world, BlockPos pos) {
        Set<Network> networkCandidates = new HashSet<>();

        for (Pipe pipe : candidates) {
            networkCandidates.add(pipe.getNetwork());
        }

        Iterator<Network> networks = networkCandidates.iterator();

        Network mainNetwork = networks.next();

        while (networks.hasNext()) {
            removeNetwork(networks.next());
        }

        mainNetwork.scanGraph(world, pos);
    }

    public void addPipe(Pipe pipe) {
        if (pipes.containsKey(pipe.getPos())) {
            throw new RuntimeException("Pipe at " + pipe.getPos() + " already exists");
        }

        pipes.put(pipe.getPos(), pipe);

        LOGGER.debug("Pipe added at {}", pipe.getPos());

        markDirty();

        Set<Pipe> adjacentPipes = findAdjacentPipes(pipe.getPos());

        if (adjacentPipes.isEmpty()) {
            formNetworkAt(pipe.getWorld(), pipe.getPos());
        } else {
            mergeNetworksIntoOneAt(adjacentPipes, pipe.getWorld(), pipe.getPos());
        }
    }

    private Set<Pipe> findAdjacentPipes(BlockPos pos) {
        Set<Pipe> pipes = new HashSet<>();

        for (Direction dir : Direction.values()) {
            Pipe pipe = getPipe(pos.offset(dir));

            if (pipe != null) {
                pipes.add(pipe);
            }
        }

        return pipes;
    }

    @Nullable
    private Pipe findFirstAdjacentPipe(BlockPos pos) {
        for (Direction dir : Direction.values()) {
            Pipe pipe = getPipe(pos.offset(dir));

            if (pipe != null) {
                return pipe;
            }
        }

        return null;
    }

    public void removePipe(Pipe pipe) {
        if (!pipes.containsKey(pipe.getPos())) {
            throw new RuntimeException("Pipe at " + pipe.getPos() + " was not found");
        }

        pipes.remove(pipe.getPos());

        LOGGER.debug("Pipe removed at {}", pipe.getPos());

        markDirty();

        splitNetworks(pipe.getPos());
    }

    private void splitNetworks(BlockPos pos) {
        // We can assume all adjacent pipes shared the same network with the removed pipe.
        // That means it doesn't matter which pipe network we use for splitting, we'll take the first found one.
        Pipe otherPipeInNetwork = findFirstAdjacentPipe(pos);

        if (otherPipeInNetwork != null) {
            NetworkGraphScanner result = otherPipeInNetwork.getNetwork().scanGraph(
                otherPipeInNetwork.getWorld(),
                otherPipeInNetwork.getPos()
            );

            for (Pipe removed : result.getRemovedPipes()) {
                // It's obvious that our removed pipe is removed.
                // We don't want to create a new network for this one.
                if (removed.getPos().equals(pos)) {
                    continue;
                }

                // The formNetworkAt call below can let these removed pipes join a network again.
                // We only have to form a new network when necessary, hence the null check.
                if (removed.getNetwork() == null) {
                    formNetworkAt(removed.getWorld(), removed.getPos());
                }
            }
        }
    }

    @Nullable
    public Pipe getPipe(BlockPos pos) {
        return pipes.get(pos);
    }

    @Override
    public void read(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return new CompoundNBT();
    }
}
