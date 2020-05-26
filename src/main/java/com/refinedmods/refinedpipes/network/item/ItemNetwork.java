package com.refinedmods.refinedpipes.network.item;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.refinedmods.refinedpipes.network.item.routing.DestinationPathCache;
import com.refinedmods.refinedpipes.network.item.routing.DestinationPathCacheFactory;
import com.refinedmods.refinedpipes.network.item.routing.EdgeFactory;
import com.refinedmods.refinedpipes.network.pipe.Destination;
import com.refinedmods.refinedpipes.network.pipe.DestinationType;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.routing.Edge;
import com.refinedmods.refinedpipes.routing.Graph;
import com.refinedmods.refinedpipes.routing.Node;
import com.refinedmods.refinedpipes.routing.NodeIndex;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemNetwork extends Network {
    public static final ResourceLocation TYPE = new ResourceLocation(RefinedPipes.ID, "item");

    private DestinationPathCache destinationPathCache;

    public ItemNetwork(BlockPos originPos, String id) {
        super(originPos, id);
    }

    @Override
    public NetworkGraphScannerResult scanGraph(World world, BlockPos pos) {
        NetworkGraphScannerResult result = super.scanGraph(world, pos);

        updateRouting(result, graph.getDestinations(DestinationType.ITEM_HANDLER));

        return result;
    }

    @Override
    public void onMergedWith(Network mainNetwork) {

    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    private void updateRouting(NetworkGraphScannerResult result, List<Destination> destinations) {
        List<Node<BlockPos>> nodes = buildNodes(result.getFoundPipes());

        NodeIndex<BlockPos> nodeIndex = NodeIndex.of(nodes);

        EdgeFactory edgeFactory = new EdgeFactory(nodeIndex, result.getRequests());
        List<Edge<BlockPos>> edges = edgeFactory.create();

        Graph<BlockPos> graph = new Graph<>(nodes, edges);

        DestinationPathCacheFactory destinationPathCacheFactory = new DestinationPathCacheFactory(graph, nodeIndex, destinations);

        this.destinationPathCache = destinationPathCacheFactory.create();
    }

    private List<Node<BlockPos>> buildNodes(Set<Pipe> pipes) {
        return pipes.stream().map(p -> new Node<>(p.getPos())).collect(Collectors.toList());
    }

    public DestinationPathCache getDestinationPathCache() {
        return destinationPathCache;
    }
}
