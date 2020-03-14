package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.graph.scanner.NetworkGraphScannerRequest;
import com.raoulvdberge.refinedpipes.network.route.Edge;
import com.raoulvdberge.refinedpipes.network.route.NodeIndex;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EdgeFactory {
    private static final Logger LOGGER = LogManager.getLogger(EdgeFactory.class);

    private final NodeIndex<BlockPos> nodeIndex;
    private final List<NetworkGraphScannerRequest> allRequests;

    public EdgeFactory(NodeIndex<BlockPos> nodeIndex, List<NetworkGraphScannerRequest> allRequests) {
        this.nodeIndex = nodeIndex;
        this.allRequests = allRequests;
    }

    public List<Edge<BlockPos>> create() {
        List<Edge<BlockPos>> edges = new ArrayList<>();

        for (NetworkGraphScannerRequest request : allRequests) {
            if (request.isSuccessful() && request.getParent() != null) {
                BlockPos origin = request.getParent().getPos();
                BlockPos destination = request.getPos();

                LOGGER.debug("Connecting " + origin + " to " + destination);

                edges.add(new Edge<>(
                    "Edge",
                    nodeIndex.getNode(origin),
                    nodeIndex.getNode(destination),
                    1
                ));

                edges.add(new Edge<>(
                    "Edge",
                    nodeIndex.getNode(destination),
                    nodeIndex.getNode(origin),
                    1
                ));
            }
        }

        return edges;
    }
}
