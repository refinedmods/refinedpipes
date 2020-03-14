package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.graph.scanner.NetworkGraphScanner;
import com.raoulvdberge.refinedpipes.network.graph.scanner.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.route.Edge;
import com.raoulvdberge.refinedpipes.network.route.Graph;
import com.raoulvdberge.refinedpipes.network.route.Node;
import com.raoulvdberge.refinedpipes.network.route.NodeIndex;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NetworkGraph {
    private final Network network;

    private Set<Pipe> pipes = new HashSet<>();
    private DestinationPathCache<IItemHandler> destinationPathCache;

    public NetworkGraph(Network network) {
        this.network = network;
    }

    public NetworkGraphScannerResult scan(World originWorld, BlockPos originPos) {
        NetworkGraphScanner scanner = new NetworkGraphScanner(pipes);

        NetworkGraphScannerResult result = scanner.scanAt(originWorld, originPos);

        this.pipes = result.getFoundPipes();

        result.getNewPipes().forEach(p -> p.joinNetwork(network));
        result.getRemovedPipes().forEach(Pipe::leaveNetwork);

        updateRouting(result);

        return result;
    }

    private void updateRouting(NetworkGraphScannerResult result) {
        List<Node<BlockPos>> nodes = buildNodes();

        NodeIndex<BlockPos> nodeIndex = NodeIndex.of(nodes);

        EdgeFactory edgeFactory = new EdgeFactory(nodeIndex, result.getRequests());
        List<Edge<BlockPos>> edges = edgeFactory.create();

        Graph<BlockPos> graph = new Graph<>(nodes, edges);

        DestinationPathCacheFactory<IItemHandler> destinationPathCacheFactory = new DestinationPathCacheFactory<>(graph, nodeIndex, result.getDestinations());

        this.destinationPathCache = destinationPathCacheFactory.create();
    }

    private List<Node<BlockPos>> buildNodes() {
        return pipes.stream().map(p -> new Node<>(p.getPos())).collect(Collectors.toList());
    }

    public Set<Pipe> getPipes() {
        return pipes;
    }

    public DestinationPathCache<IItemHandler> getDestinationPathCache() {
        return destinationPathCache;
    }
}
