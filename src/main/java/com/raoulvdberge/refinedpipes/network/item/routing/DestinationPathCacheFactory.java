package com.raoulvdberge.refinedpipes.network.item.routing;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemDestination;
import com.raoulvdberge.refinedpipes.routing.*;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DestinationPathCacheFactory {
    private static final Logger LOGGER = LogManager.getLogger(DestinationPathCacheFactory.class);

    private final Graph<BlockPos> graph;
    private final NodeIndex<BlockPos> nodeIndex;
    private final Set<ItemDestination> destinations;

    public DestinationPathCacheFactory(Graph<BlockPos> graph, NodeIndex<BlockPos> nodeIndex, Set<ItemDestination> destinations) {
        this.graph = graph;
        this.nodeIndex = nodeIndex;
        this.destinations = destinations;
    }

    public DestinationPathCache create() {
        DestinationPathCache cache = new DestinationPathCache();

        for (Node<BlockPos> node : graph.getNodes()) {
            DijkstraAlgorithm<BlockPos> dijkstra = new DijkstraAlgorithm<>(graph);

            dijkstra.execute(node);

            for (ItemDestination destination : destinations) {
                if (destination.getConnectedPipe().getPos().equals(node.getId())) {
                    List<Node<BlockPos>> nodes = new ArrayList<>();
                    nodes.add(node);

                    cache.addPath(node.getId(), destination, new Path<>(nodes));
                    continue;
                }

                Pipe connectedPipe = destination.getConnectedPipe();
                Node<BlockPos> connectedPipeNode = nodeIndex.getNode(connectedPipe.getPos());

                if (connectedPipeNode == null) {
                    LOGGER.error("Connected pipe has no node! At " + connectedPipe.getPos());
                    continue;
                }

                List<Node<BlockPos>> path = dijkstra.getPath(connectedPipeNode);

                if (path != null) {
                    cache.addPath(node.getId(), destination, new Path<>(path));

                    LOGGER.debug("Computed path from " + node.getId() + " to " + connectedPipeNode.getId() + " -> " + path.size() + " nodes");
                } else {
                    LOGGER.error("Could not find path from " + node.getId() + " to " + connectedPipeNode.getId());
                }
            }
        }

        return cache;
    }
}
