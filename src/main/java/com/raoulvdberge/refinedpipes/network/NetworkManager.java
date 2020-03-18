package com.raoulvdberge.refinedpipes.network;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.graph.scanner.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
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
    private final Map<String, Network> networks = new HashMap<>();
    private final Map<BlockPos, Pipe> pipes = new HashMap<>();

    public NetworkManager(String name, World world) {
        super(name);

        this.world = world;
    }

    public void addNetwork(Network network) {
        if (networks.containsKey(network.getId())) {
            throw new RuntimeException("Duplicate network " + network.getId());
        }

        networks.put(network.getId(), network);

        LOGGER.debug("Network {} created", network.getId());

        markDirty();
    }

    public void removeNetwork(String id) {
        if (!networks.containsKey(id)) {
            throw new RuntimeException("Network " + id + " not found");
        }

        networks.remove(id);

        LOGGER.debug("Network {} removed", id);

        markDirty();
    }

    private void formNetworkAt(World world, BlockPos pos) {
        Network network = new Network(pos);

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
            removeNetwork(networks.next().getId());
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
            throw new RuntimeException("Pipe has no network"); // TODO java.lang.RuntimeException: Pipe has no network
        }

        pipes.remove(pipe.getPos());

        LOGGER.debug("Pipe removed at {}", pipe.getPos());

        markDirty();

        splitNetworks(pipe);
    }

    private void splitNetworks(Pipe originPipe) {
        // Sanity checks
        for (Pipe adjacent : findAdjacentPipes(originPipe.getPos())) {
            if (adjacent.getNetwork() == null) {
                throw new RuntimeException("Adjacent pipe has no network");
            }

            if (originPipe.getNetwork() != originPipe.getNetwork()) {
                throw new RuntimeException("The origin pipe network is different than the adjacent pipe network");
            }
        }

        // We can assume all adjacent pipes shared the same network with the removed pipe.
        // That means it doesn't matter which pipe network we use for splitting, we'll take the first found one.
        Pipe otherPipeInNetwork = findFirstAdjacentPipe(originPipe.getPos());

        if (otherPipeInNetwork != null) {
            NetworkGraphScannerResult result = otherPipeInNetwork.getNetwork().scanGraph(
                otherPipeInNetwork.getWorld(),
                otherPipeInNetwork.getPos()
            );

            // For sanity checking
            boolean foundRemovedPipe = false;

            for (Pipe removed : result.getRemovedPipes()) {
                // It's obvious that our removed pipe is removed.
                // We don't want to create a new splitted network for this one.
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

            removeNetwork(originPipe.getNetwork().getId());
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

    @Nullable
    public Network getNetwork(String id) {
        return networks.get(id);
    }

    public Collection<Network> getNetworks() {
        return networks.values();
    }

    @Override
    public void read(CompoundNBT tag) {
        ListNBT pipes = tag.getList("pipes", Constants.NBT.TAG_COMPOUND);
        for (INBT item : pipes) {
            Pipe pipe = Pipe.fromNbt(world, (CompoundNBT) item);

            this.pipes.put(pipe.getPos(), pipe);
        }

        ListNBT nets = tag.getList("networks", Constants.NBT.TAG_COMPOUND);
        for (INBT item : nets) {
            Network network = Network.fromNbt((CompoundNBT) item);

            networks.put(network.getId(), network);
        }

        LOGGER.debug("Read {} pipes", pipes.size());
        LOGGER.debug("Read {} networks", networks.size());
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        ListNBT pipes = new ListNBT();
        this.pipes.values().forEach(p -> pipes.add(p.writeToNbt(new CompoundNBT())));
        tag.put("pipes", pipes);

        ListNBT networks = new ListNBT();
        this.networks.values().forEach(n -> networks.add(n.writeToNbt(new CompoundNBT())));
        tag.put("networks", networks);

        return tag;
    }
}
