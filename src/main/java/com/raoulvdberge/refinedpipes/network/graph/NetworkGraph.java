package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.DestinationType;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class NetworkGraph {
    private final Network network;

    private Set<Pipe> pipes = new HashSet<>();
    private Map<DestinationType, List<Destination>> destinations = new HashMap<>();

    public NetworkGraph(Network network) {
        this.network = network;
    }

    public NetworkGraphScannerResult scan(World originWorld, BlockPos originPos) {
        NetworkGraphScanner scanner = new NetworkGraphScanner(pipes, network.getType());

        NetworkGraphScannerResult result = scanner.scanAt(originWorld, originPos);

        this.pipes = result.getFoundPipes();

        result.getNewPipes().forEach(p -> p.joinNetwork(network));
        result.getRemovedPipes().forEach(Pipe::leaveNetwork);

        destinations.clear();

        for (Destination destination : result.getDestinations()) {
            destinations.computeIfAbsent(destination.getType(), type -> new ArrayList<>()).add(destination);
        }

        return result;
    }

    public Set<Pipe> getPipes() {
        return pipes;
    }

    public List<Destination> getDestinations(DestinationType type) {
        return destinations.getOrDefault(type, Collections.emptyList());
    }
}
