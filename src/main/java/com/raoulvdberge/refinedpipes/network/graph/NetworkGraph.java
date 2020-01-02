package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.Pipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class NetworkGraph {
    private final Network network;
    private Set<Pipe> pipes = new HashSet<>();

    public NetworkGraph(Network network) {
        this.network = network;
    }

    public NetworkGraphScanner scan(World originWorld, BlockPos originPos) {
        NetworkGraphScanner scanner = new NetworkGraphScanner(pipes);

        scanner.getRequests().add(new NetworkGraphScannerRequest(originWorld, originPos));

        NetworkGraphScannerRequest request;
        while ((request = scanner.getRequests().poll()) != null) {
            scanner.scanAt(request.getWorld(), request.getPos());
        }

        this.pipes = scanner.getFoundPipes();

        scanner.getNewPipes().forEach(p -> p.joinNetwork(network));
        scanner.getRemovedPipes().forEach(Pipe::leaveNetwork);

        return scanner;
    }
}
