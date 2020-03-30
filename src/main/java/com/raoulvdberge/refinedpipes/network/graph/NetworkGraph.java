package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidDestination;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class NetworkGraph {
    private final Network network;

    private Set<Pipe> pipes = new HashSet<>();
    private Set<FluidDestination> fluidDestinations = new HashSet<>();

    public NetworkGraph(Network network) {
        this.network = network;
    }

    public NetworkGraphScannerResult scan(World originWorld, BlockPos originPos) {
        NetworkGraphScanner scanner = new NetworkGraphScanner(pipes, network.getType());

        NetworkGraphScannerResult result = scanner.scanAt(originWorld, originPos);

        this.pipes = result.getFoundPipes();

        result.getNewPipes().forEach(p -> p.joinNetwork(network));
        result.getRemovedPipes().forEach(Pipe::leaveNetwork);

        this.fluidDestinations = result.getFluidDestinations();

        return result;
    }

    public Set<Pipe> getPipes() {
        return pipes;
    }

    public Set<FluidDestination> getFluidDestinations() {
        return fluidDestinations;
    }
}
