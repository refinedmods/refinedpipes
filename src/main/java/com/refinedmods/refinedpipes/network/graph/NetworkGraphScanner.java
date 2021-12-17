package com.refinedmods.refinedpipes.network.graph;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.Destination;
import com.refinedmods.refinedpipes.network.pipe.DestinationType;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    public NetworkGraphScannerResult scanAt(Level level, BlockPos pos) {
        addRequest(new NetworkGraphScannerRequest(level, pos, null, null));

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
        Pipe pipe = NetworkManager.get(request.getLevel()).getPipe(request.getPos());

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
                        request.getLevel(),
                        request.getPos().relative(dir),
                        dir,
                        request
                    ));
                }
            }
        } else if (request.getParent() != null) { // This can NOT be called on pipe positions! (causes problems with block entities getting invalidated/validates when it shouldn't)
            // We can NOT have the TE capability checks always run regardless of whether there was a pipe or not.
            // Otherwise we have this loop: pipe gets placed -> network gets scanned -> TEs get checked -> it might check the TE we just placed
            // -> the newly created TE can be created in immediate mode -> TE#validate is called again -> TE#remove is called again!

            Pipe connectedPipe = NetworkManager.get(request.getLevel()).getPipe(request.getParent().getPos());

            // If this destination is connected to a pipe with an attachment, then this is not a valid destination.
            if (!connectedPipe.getAttachmentManager().hasAttachment(request.getDirection())) {
                BlockEntity blockEntity = request.getLevel().getBlockEntity(request.getPos());

                if (blockEntity != null) {
                    blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, request.getDirection().getOpposite())
                        .ifPresent(itemHandler -> destinations.add(new Destination(DestinationType.ITEM_HANDLER, request.getPos(), request.getDirection(), connectedPipe)));

                    blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, request.getDirection().getOpposite())
                        .ifPresent(fluidHandler -> destinations.add(new Destination(DestinationType.FLUID_HANDLER, request.getPos(), request.getDirection(), connectedPipe)));

                    blockEntity.getCapability(CapabilityEnergy.ENERGY, request.getDirection().getOpposite())
                        .ifPresent(energyStorage -> {
                            if (!(energyStorage instanceof EnergyPipeEnergyStorage)) {
                                destinations.add(new Destination(DestinationType.ENERGY_STORAGE, request.getPos(), request.getDirection(), connectedPipe));
                            }
                        });
                }
            }
        }
    }

    private void addRequest(NetworkGraphScannerRequest request) {
        requests.add(request);
        allRequests.add(request);
    }
}
