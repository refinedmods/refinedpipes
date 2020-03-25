package com.raoulvdberge.refinedpipes.network.graph.scanner;

import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.ItemPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.*;

public class NetworkGraphScanner {
    private final Set<ItemPipe> foundPipes = new HashSet<>();
    private final Set<ItemPipe> newPipes = new HashSet<>();
    private final Set<ItemPipe> removedPipes = new HashSet<>();
    private final Set<Destination> destinations = new HashSet<>();
    private final Set<ItemPipe> currentPipes;

    private final List<NetworkGraphScannerRequest> allRequests = new ArrayList<>();
    private final Queue<NetworkGraphScannerRequest> requests = new ArrayDeque<>();

    public NetworkGraphScanner(Set<ItemPipe> currentPipes) {
        this.currentPipes = currentPipes;
        this.removedPipes.addAll(currentPipes);
    }

    public NetworkGraphScannerResult scanAt(World world, BlockPos pos) {
        addRequest(new NetworkGraphScannerRequest(world, pos, null, null));

        NetworkGraphScannerRequest request;
        while ((request = requests.poll()) != null) {
            singleScanAt(request);
        }

        return new NetworkGraphScannerResult(
            foundPipes,
            newPipes,
            removedPipes,
            destinations,
            allRequests
        );
    }

    private void singleScanAt(NetworkGraphScannerRequest request) {
        ItemPipe pipe = NetworkManager.get(request.getWorld()).getPipe(request.getPos());

        if (pipe != null) {
            if (foundPipes.add(pipe)) {
                if (!currentPipes.contains(pipe)) {
                    newPipes.add(pipe);
                }

                removedPipes.remove(pipe);

                request.setSuccessful(true);

                for (Direction dir : Direction.values()) {
                    addRequest(new NetworkGraphScannerRequest(
                        request.getWorld(),
                        request.getPos().offset(dir),
                        dir,
                        request
                    ));
                }
            }
        } else if (request.getParent() != null) {
            ItemPipe connectedPipe = NetworkManager.get(request.getWorld()).getPipe(request.getParent().getPos());

            TileEntity tile = request.getWorld().getTileEntity(request.getPos());

            if (tile != null) {
                tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, request.getDirection())
                    .ifPresent(itemHandler -> destinations.add(new Destination(request.getPos(), request.getDirection(), connectedPipe)));
            }
        }
    }

    private void addRequest(NetworkGraphScannerRequest request) {
        requests.add(request);
        allRequests.add(request);
    }
}
