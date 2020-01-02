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
        LOGGER.debug("Network created " + network.getId());

        networks.add(network);
        markDirty();
    }

    public void addPipe(Pipe pipe) {
        LOGGER.debug("Pipe added @ " + pipe.getPos());

        pipes.put(pipe.getPos(), pipe);
        markDirty();

        Set<Pipe> adjacent = findAdjacentPipes(pipe.getPos());
        if (adjacent.isEmpty()) {
            Network network = formNetwork();
            network.scanGraph(pipe.getWorld(), pipe.getPos());
        } else {
            Network network = joinExistingNetwork(adjacent);
            network.scanGraph(pipe.getWorld(), pipe.getPos());
        }
    }

    private Network joinExistingNetwork(Set<Pipe> pipes) {
        Set<Network> adjacentNetworks = new HashSet<>();
        for (Pipe pipe : pipes) {
            adjacentNetworks.add(pipe.getNetwork());
        }

        Iterator<Network> nets = adjacentNetworks.iterator();
        Network mainNetwork = nets.next();

        while (nets.hasNext()) {
            removeNetwork(nets.next());
        }

        return mainNetwork;
    }

    private Network formNetwork() {
        Network network = new Network();
        addNetwork(network);
        return network;
    }

    private Set<Pipe> findAdjacentPipes(BlockPos pos) {
        Set<Pipe> pipes = new HashSet<>();

        for (Direction dir : Direction.values()) {
            BlockPos newPos = pos.offset(dir);

            if (this.pipes.containsKey(newPos)) {
                pipes.add(this.pipes.get(newPos));
            }
        }

        return pipes;
    }

    @Nullable
    private Pipe findFirstAdjacentPipe(BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos newPos = pos.offset(dir);

            if (this.pipes.containsKey(newPos)) {
                return this.pipes.get(newPos);
            }
        }

        return null;
    }

    public void removePipe(Pipe pipe) {
        LOGGER.debug("Pipe removed @ " + pipe.getPos());

        pipes.remove(pipe.getPos());
        markDirty();

        splitIntoMultipleNetworks(pipe.getPos());
    }

    private void splitIntoMultipleNetworks(BlockPos pos) {
        // we can assume all adjacent pipes share(d) the same network
        // we can just use the first neighboring one.
        Pipe otherPipeFromNetwork = findFirstAdjacentPipe(pos);
        if (otherPipeFromNetwork != null) {
            NetworkGraphScanner result = otherPipeFromNetwork.getNetwork().scanGraph(
                otherPipeFromNetwork.getWorld(),
                otherPipeFromNetwork.getPos()
            );

            for (Pipe removed : result.getRemovedPipes()) {
                // obviously the pipe we just removed has been removed - ignore it
                if (!removed.getPos().equals(pos)) {
                    // the scanGraph call below can let these removed pipes join a network again.
                    // we only have to create a new network when necessary, hence the null check.
                    if (removed.getNetwork() == null) {
                        formNetwork().scanGraph(removed.getWorld(), removed.getPos());
                    }
                }
            }
        }
    }

    @Nullable
    public Pipe getPipe(BlockPos pos) {
        return pipes.get(pos);
    }

    public void removeNetwork(Network network) {
        LOGGER.debug("Network removed " + network.getId());

        networks.remove(network);
        markDirty();
    }

    @Override
    public void read(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return new CompoundNBT();
    }
}
