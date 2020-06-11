package com.refinedmods.refinedpipes.network;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.PipeFactory;
import com.refinedmods.refinedpipes.network.pipe.PipeRegistry;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
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

    private void formNetworkAt(World world, BlockPos pos, ResourceLocation type) {
        Network network = NetworkRegistry.INSTANCE.getFactory(type).create(pos);

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

        Set<Network> mergedNetworks = new HashSet<>();

        while (networks.hasNext()) {
            // Remove all the other networks.
            Network otherNetwork = networks.next();

            boolean canMerge = mainNetwork.getType().equals(otherNetwork.getType());

            if (canMerge) {
                mergedNetworks.add(otherNetwork);

                removeNetwork(otherNetwork.getId());
            }
        }

        mainNetwork.scanGraph(world, pos);

        mergedNetworks.forEach(n -> n.onMergedWith(mainNetwork));
    }

    public void addPipe(Pipe pipe) {
        if (pipes.containsKey(pipe.getPos())) {
            throw new RuntimeException("Pipe at " + pipe.getPos() + " already exists");
        }

        pipes.put(pipe.getPos(), pipe);

        LOGGER.debug("Pipe added at {}", pipe.getPos());

        markDirty();

        Set<Pipe> adjacentPipes = findAdjacentPipes(pipe.getPos(), pipe.getNetworkType());

        if (adjacentPipes.isEmpty()) {
            formNetworkAt(pipe.getWorld(), pipe.getPos(), pipe.getNetworkType());
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
            LOGGER.warn("Removed pipe at {} has no associated network", pipe.getPos());
        }

        pipes.remove(pipe.getPos());

        LOGGER.debug("Pipe removed at {}", pipe.getPos());

        markDirty();

        if (pipe.getNetwork() != null) {
            splitNetworks(pipe);
        }
    }

    private void splitNetworks(Pipe originPipe) {
        // Sanity checks
        for (Pipe adjacent : findAdjacentPipes(originPipe.getPos(), originPipe.getNetworkType())) {
            if (adjacent.getNetwork() == null) {
                throw new RuntimeException("Adjacent pipe has no network");
            }

            if (adjacent.getNetwork() != originPipe.getNetwork()) {
                throw new RuntimeException("The origin pipe network is different than the adjacent pipe network");
            }
        }

        // We can assume all adjacent pipes (with the same network type) share the same network with the removed pipe.
        // That means it doesn't matter which pipe network we use for splitting, we'll take the first found one.
        Pipe otherPipeInNetwork = findFirstAdjacentPipe(originPipe.getPos(), originPipe.getNetworkType());

        if (otherPipeInNetwork != null) {
            otherPipeInNetwork.getNetwork().setOriginPos(otherPipeInNetwork.getPos());
            markDirty();

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
                    formNetworkAt(removed.getWorld(), removed.getPos(), removed.getNetworkType());
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

    private Set<Pipe> findAdjacentPipes(BlockPos pos, ResourceLocation networkType) {
        Set<Pipe> pipes = new HashSet<>();

        for (Direction dir : Direction.values()) {
            Pipe pipe = getPipe(pos.offset(dir));

            if (pipe != null && pipe.getNetworkType().equals(networkType)) {
                pipes.add(pipe);
            }
        }

        return pipes;
    }

    @Nullable
    private Pipe findFirstAdjacentPipe(BlockPos pos, ResourceLocation networkType) {
        for (Direction dir : Direction.values()) {
            Pipe pipe = getPipe(pos.offset(dir));

            if (pipe != null && pipe.getNetworkType().equals(networkType)) {
                return pipe;
            }
        }

        return null;
    }

    @Nullable
    public Pipe getPipe(BlockPos pos) {
        return pipes.get(pos);
    }

    public Collection<Network> getNetworks() {
        return networks.values();
    }

    @Override
    public void read(CompoundNBT tag) {
        ListNBT pipes = tag.getList("pipes", Constants.NBT.TAG_COMPOUND);
        for (INBT pipeTag : pipes) {
            CompoundNBT pipeTagCompound = (CompoundNBT) pipeTag;

            // @BC
            ResourceLocation factoryId = pipeTagCompound.contains("id") ? new ResourceLocation(pipeTagCompound.getString("id")) : ItemPipe.ID;

            PipeFactory factory = PipeRegistry.INSTANCE.getFactory(factoryId);
            if (factory == null) {
                LOGGER.warn("Pipe {} no longer exists", factoryId.toString());
                continue;
            }

            Pipe pipe = factory.createFromNbt(world, pipeTagCompound);

            this.pipes.put(pipe.getPos(), pipe);
        }

        ListNBT nets = tag.getList("networks", Constants.NBT.TAG_COMPOUND);
        for (INBT netTag : nets) {
            CompoundNBT netTagCompound = (CompoundNBT) netTag;
            if (!netTagCompound.contains("type")) {
                LOGGER.warn("Skipping network without type");
                continue;
            }

            ResourceLocation type = new ResourceLocation(netTagCompound.getString("type"));

            NetworkFactory factory = NetworkRegistry.INSTANCE.getFactory(type);
            if (factory == null) {
                LOGGER.warn("Unknown network type {}", type.toString());
                continue;
            }

            Network network = factory.create(netTagCompound);

            networks.put(network.getId(), network);
        }

        LOGGER.debug("Read {} pipes", pipes.size());
        LOGGER.debug("Read {} networks", networks.size());
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        ListNBT pipes = new ListNBT();
        this.pipes.values().forEach(p -> {
            CompoundNBT pipeTag = new CompoundNBT();
            pipeTag.putString("id", p.getId().toString());
            pipes.add(p.writeToNbt(pipeTag));
        });
        tag.put("pipes", pipes);

        ListNBT networks = new ListNBT();
        this.networks.values().forEach(n -> {
            CompoundNBT networkTag = new CompoundNBT();
            networkTag.putString("type", n.getType().toString());
            networks.add(n.writeToNbt(networkTag));
        });
        tag.put("networks", networks);

        return tag;
    }
}
