package com.raoulvdberge.refinedpipes.network;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScannerResult;
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

        return world.getSavedData().getOrCreate(() -> new NetworkManager(name), name);
    }

    private final Set<Network> networks = new HashSet<>();
    private final Map<BlockPos, Pipe> pipes = new HashMap<>();

    public NetworkManager(String name) {
        super(name);
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

    private void mergeNetworksIntoOne(Set<Pipe> candidates, World world, BlockPos pos) {
        if (candidates.isEmpty()) {
            throw new RuntimeException("Cannot merge networks: no candidates");
        }

        Set<Network> networkCandidates = new HashSet<>();

        for (Pipe candidate : candidates) {
            if (candidate.getNetwork() == null) {
                throw new RuntimeException("Pipe network is null!");
            }

            networkCandidates.add(candidate.getNetwork());
        }

        Iterator<Network> networks = networkCandidates.iterator();

        Network mainNetwork = networks.next();

        while (networks.hasNext()) {
            // Remove all the other networks.
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
            mergeNetworksIntoOne(adjacentPipes, pipe.getWorld(), pipe.getPos());
        }
    }

    public void removePipe(BlockPos pos) {
        Pipe pipe = getPipe(pos);
        if (pipe == null) {
            throw new RuntimeException("Pipe at " + pos + " was not found");
        }

        if (pipe.getNetwork() == null) {
            throw new RuntimeException("Pipe has no network");
        }

        pipes.remove(pipe.getPos());

        LOGGER.debug("Pipe removed at {}", pipe.getPos());

        markDirty();

        splitNetworks(pipe);
    }

    private void splitNetworks(Pipe originPipe) {
        // We can assume all adjacent pipes shared the same network with the removed pipe.
        // That means it doesn't matter which pipe network we use for splitting, we'll take the first found one.
        Pipe otherPipeInNetwork = findFirstAdjacentPipe(originPipe.getPos());

        if (otherPipeInNetwork != null) {
            if (otherPipeInNetwork.getNetwork() == null) {
                throw new RuntimeException("Pipe network is null!");
            }

            if (otherPipeInNetwork.getNetwork() != originPipe.getNetwork()) {
                throw new RuntimeException("The origin pipe network is different than the adjacent pipe network");
            }

            NetworkGraphScannerResult result = otherPipeInNetwork.getNetwork().scanGraph(
                otherPipeInNetwork.getWorld(),
                otherPipeInNetwork.getPos()
            );

            // Only for validation purposes.
            boolean foundRemovedPipe = false;

            for (Pipe removed : result.getRemovedPipes()) {
                // It's obvious that our removed pipe is removed.
                // We don't want to create a new network for this one.
                if (removed.getPos().equals(originPipe.getPos())) {
                    foundRemovedPipe = true;
                    continue;
                }

                // The formNetworkAt call below can let these removed pipes join a network again.
                // We only have to form a new network when necessary, hence the null check.
                if (removed.getNetwork() == null) {
                    formNetworkAt(removed.getWorld(), removed.getPos());
                }
            }

            if (!foundRemovedPipe) {
                throw new RuntimeException("Didn't find removed pipe when splitting network");
            }
        } else {
            LOGGER.debug("Removing empty network {}", originPipe.getNetwork().getId());

            removeNetwork(originPipe.getNetwork());
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
