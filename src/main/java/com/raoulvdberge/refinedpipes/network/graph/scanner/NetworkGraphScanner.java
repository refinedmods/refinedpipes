package com.raoulvdberge.refinedpipes.network.graph.scanner;

import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidDestination;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemDestination;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.*;

public class NetworkGraphScanner {
    private final Set<Pipe> foundPipes = new HashSet<>();
    private final Set<Pipe> newPipes = new HashSet<>();
    private final Set<Pipe> removedPipes = new HashSet<>();
    private final Set<ItemDestination> itemDestinations = new HashSet<>();
    private final Set<FluidDestination> fluidDestinations = new HashSet<>();
    private final Set<Pipe> currentPipes;

    private Pipe firstFoundPipe;

    private final List<NetworkGraphScannerRequest> allRequests = new ArrayList<>();
    private final Queue<NetworkGraphScannerRequest> requests = new ArrayDeque<>();

    public NetworkGraphScanner(Set<Pipe> currentPipes) {
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
            itemDestinations,
            fluidDestinations,
            allRequests
        );
    }

    private void singleScanAt(NetworkGraphScannerRequest request) {
        Pipe pipe = NetworkManager.get(request.getWorld()).getPipe(request.getPos());

        if (pipe != null) {
            if (firstFoundPipe != null && !firstFoundPipe.canFormNetworkWith(pipe)) {
                return;
            }

            if (foundPipes.add(pipe)) {
                if (firstFoundPipe == null) {
                    firstFoundPipe = pipe;
                }

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
            Pipe connectedPipe = NetworkManager.get(request.getWorld()).getPipe(request.getParent().getPos());

            // If this item handler is connected to a pipe with an attachment, then this is not a valid destination.
            if (!connectedPipe.getAttachmentManager().hasAttachment(request.getDirection())) {
                TileEntity tile = request.getWorld().getTileEntity(request.getPos());

                if (tile != null) {
                    tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, request.getDirection().getOpposite())
                        .ifPresent(itemHandler -> itemDestinations.add(new ItemDestination(request.getPos(), request.getDirection(), connectedPipe)));

                    tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, request.getDirection().getOpposite())
                        .ifPresent(fluidHandler -> fluidDestinations.add(new FluidDestination(request.getPos(), request.getDirection(), connectedPipe)));
                }
            }
        }
    }

    private void addRequest(NetworkGraphScannerRequest request) {
        requests.add(request);
        allRequests.add(request);
    }
}
