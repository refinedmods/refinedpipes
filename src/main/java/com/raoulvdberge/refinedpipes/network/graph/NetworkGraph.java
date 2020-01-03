package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.route.DijkstraAlgorithm;
import com.raoulvdberge.refinedpipes.network.route.Edge;
import com.raoulvdberge.refinedpipes.network.route.Graph;
import com.raoulvdberge.refinedpipes.network.route.Node;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class NetworkGraph {
    private static final Logger LOGGER = LogManager.getLogger(NetworkGraph.class);

    private final Network network;

    private Set<Pipe> pipes = new HashSet<>();
    private DijkstraAlgorithm<BlockPos> routing;

    public NetworkGraph(Network network) {
        this.network = network;
    }

    public Set<Pipe> getPipes() {
        return pipes;
    }

    public NetworkGraphScannerResult scan(World originWorld, BlockPos originPos) {
        NetworkGraphScanner scanner = new NetworkGraphScanner(pipes);

        NetworkGraphScannerResult result = scanner.scanAt(originWorld, originPos);

        this.pipes = result.getFoundPipes();

        result.getNewPipes().forEach(p -> p.joinNetwork(network));
        result.getRemovedPipes().forEach(Pipe::leaveNetwork);

        updateRouting(result.getRequests());

        return result;
    }

    public void updateRouting(List<NetworkGraphScannerRequest> allRequests) {
        List<Node<BlockPos>> nodes = new ArrayList<>();
        Map<BlockPos, Node<BlockPos>> nodeMap = new HashMap<>();

        List<Edge<BlockPos>> edges = new ArrayList<>();

        for (Pipe pipe : pipes) {
            Node<BlockPos> node = new Node<>(pipe.getPos());
            nodes.add(node);
            nodeMap.put(pipe.getPos(), node);
        }

        for (NetworkGraphScannerRequest request : allRequests) {
            if (request.isSuccessful() && request.getParent() != null) {
                BlockPos origin = request.getParent().getPos();
                BlockPos destination = request.getPos();
                
                LOGGER.debug("Connecting " + origin + " to " + destination);

                edges.add(new Edge<>(
                    "Edge",
                    nodeMap.get(origin),
                    nodeMap.get(destination),
                    1
                ));
            }
        }

        this.routing = new DijkstraAlgorithm<>(new Graph<>(nodes, edges));
    }
}
