package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.route.DijkstraAlgorithm;
import com.raoulvdberge.refinedpipes.network.route.Edge;
import com.raoulvdberge.refinedpipes.network.route.Graph;
import com.raoulvdberge.refinedpipes.network.route.Node;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class NetworkGraph {
    private static final Logger LOGGER = LogManager.getLogger(NetworkGraph.class);

    private final Network network;

    private Set<Pipe> pipes = new HashSet<>();
    private Graph<BlockPos> routingGraph;
    private Map<BlockPos, Map<Destination<IItemHandler>, List<Node<BlockPos>>>> nodeToDestinationPaths;

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

        updateRouting(result);

        return result;
    }

    private void updateRouting(NetworkGraphScannerResult result) {
        List<Node<BlockPos>> nodes = buildNodes();
        Map<BlockPos, Node<BlockPos>> nodeIndex = buildNodeIndex(nodes);
        List<Edge<BlockPos>> edges = buildEdges(nodeIndex, result.getRequests());

        Graph<BlockPos> graph = new Graph<>(nodes, edges);

        this.nodeToDestinationPaths = buildNodeToDestinationPaths(graph, nodeIndex, result.getDestinations());
        this.routingGraph = graph;
    }

    public Map<BlockPos, Map<Destination<IItemHandler>, List<Node<BlockPos>>>> getNodeToDestinationPaths() {
        return nodeToDestinationPaths;
    }

    private List<Node<BlockPos>> buildNodes() {
        return pipes.stream().map(p -> new Node<>(p.getPos())).collect(Collectors.toList());
    }

    private Map<BlockPos, Node<BlockPos>> buildNodeIndex(List<Node<BlockPos>> nodes) {
        Map<BlockPos, Node<BlockPos>> nodeIndex = new HashMap<>();

        for (Node<BlockPos> node : nodes) {
            nodeIndex.put(node.getId(), node);
        }

        return nodeIndex;
    }

    private List<Edge<BlockPos>> buildEdges(Map<BlockPos, Node<BlockPos>> nodeIndex, List<NetworkGraphScannerRequest> allRequests) {
        List<Edge<BlockPos>> edges = new ArrayList<>();

        for (NetworkGraphScannerRequest request : allRequests) {
            if (request.isSuccessful() && request.getParent() != null) {
                BlockPos origin = request.getParent().getPos();
                BlockPos destination = request.getPos();

                LOGGER.debug("Connecting " + origin + " to " + destination);

                edges.add(new Edge<>(
                    "Edge",
                    nodeIndex.get(origin),
                    nodeIndex.get(destination),
                    1
                ));

                edges.add(new Edge<>(
                    "Edge",
                    nodeIndex.get(destination),
                    nodeIndex.get(origin),
                    1
                ));
            }
        }

        return edges;
    }

    private Map<BlockPos, Map<Destination<IItemHandler>, List<Node<BlockPos>>>> buildNodeToDestinationPaths(Graph<BlockPos> graph, Map<BlockPos, Node<BlockPos>> nodeIndex, Set<Destination<IItemHandler>> destinations) {
        Map<BlockPos, Map<Destination<IItemHandler>, List<Node<BlockPos>>>> paths = new HashMap<>();

        for (Node<BlockPos> node : graph.getNodes()) {
            DijkstraAlgorithm<BlockPos> dijkstra = new DijkstraAlgorithm<>(graph);

            dijkstra.execute(node);

            for (Destination<IItemHandler> destination : destinations) {
                Pipe connectedPipe = destination.getConnectedPipe();
                Node<BlockPos> connectedPipeNode = nodeIndex.get(connectedPipe.getPos());

                if (connectedPipeNode == null) {
                    LOGGER.error("Connected pipe has no node! At " + connectedPipe.getPos());
                    continue;
                }

                List<Node<BlockPos>> path = dijkstra.getPath(connectedPipeNode);

                if (path != null) {
                    paths.computeIfAbsent(node.getId(), p -> new HashMap<>()).put(destination, path);

                    LOGGER.debug("Computed path from " + node.getId() + " to " + connectedPipeNode.getId() + " -> " + path.size() + " nodes");
                } else {
                    LOGGER.error("Could not find path from " + node.getId() + " to " + connectedPipeNode.getId());
                }
            }
        }

        return paths;
    }

    public Graph<BlockPos> getRoutingGraph() {
        return routingGraph;
    }
}
