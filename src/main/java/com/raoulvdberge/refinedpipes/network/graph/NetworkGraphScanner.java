package com.raoulvdberge.refinedpipes.network.graph;

import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.DestinationType;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.*;

public class NetworkGraphScanner {
    private final Set<Pipe> foundPipes = new HashSet<>();
    private final Set<Pipe> newPipes = new HashSet<>();
    private final Set<Pipe> removedPipes = new HashSet<>();
    private final Set<Destination> destinations = new HashSet<>();
    private final Set<Pipe> currentPipes;
    private final ResourceLocation requiredNetworkType;

    private final List<NetworkGraphScannerRequest> allRequests = new ArrayList<>();
    private final Queue<NetworkGraphScannerRequest> requests = new ArrayDeque<>();

    public NetworkGraphScanner(Set<Pipe> currentPipes, ResourceLocation requiredNetworkType) {
        this.currentPipes = currentPipes;
        this.removedPipes.addAll(currentPipes);
        this.requiredNetworkType = requiredNetworkType;
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
        Pipe pipe = NetworkManager.get(request.getWorld()).getPipe(request.getPos());

        if (pipe != null) {
            if (!requiredNetworkType.equals(pipe.getNetworkType())) {
                return;
            }

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
            Pipe connectedPipe = NetworkManager.get(request.getWorld()).getPipe(request.getParent().getPos());

            // If this item handler is connected to a pipe with an attachment, then this is not a valid destination.
            if (!connectedPipe.getAttachmentManager().hasAttachment(request.getDirection())) {
                TileEntity tile = request.getWorld().getTileEntity(request.getPos());

                if (tile != null) {
                    tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, request.getDirection().getOpposite())
                        .ifPresent(itemHandler -> destinations.add(new Destination(DestinationType.ITEM_HANDLER, request.getPos(), request.getDirection(), connectedPipe)));

                    tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, request.getDirection().getOpposite())
                        .ifPresent(fluidHandler -> destinations.add(new Destination(DestinationType.FLUID_HANDLER, request.getPos(), request.getDirection(), connectedPipe)));

                    tile.getCapability(CapabilityEnergy.ENERGY, request.getDirection().getOpposite())
                        .ifPresent(energyStorage -> destinations.add(new Destination(DestinationType.ENERGY_STORAGE, request.getPos(), request.getDirection(), connectedPipe)));
                }
            }
        }
    }

    private void addRequest(NetworkGraphScannerRequest request) {
        requests.add(request);
        allRequests.add(request);
    }
}
