package com.refinedmods.refinedpipes.network.energy;

import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.refinedmods.refinedpipes.network.pipe.Destination;
import com.refinedmods.refinedpipes.network.pipe.DestinationType;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipe;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class EnergyNetwork extends Network {
    private final EnergyStorage energyStorage;
    private final EnergyPipeType pipeType;

    public EnergyNetwork(BlockPos originPos, String id, EnergyPipeType pipeType) {
        super(originPos, id);

        this.pipeType = pipeType;
        this.energyStorage = new EnergyStorage(0);
        this.energyStorage.setMaxReceive(pipeType.getCapacity());
    }

    @Override
    public NetworkGraphScannerResult scanGraph(Level world, BlockPos pos) {
        NetworkGraphScannerResult result = super.scanGraph(world, pos);

        energyStorage.setCapacityAndMaxExtract(
            result.getFoundPipes()
                .stream()
                .filter(p -> p instanceof EnergyPipe)
                .mapToInt(p -> ((EnergyPipe) p).getType().getCapacity())
                .sum()
        );

        if (energyStorage.getEnergyStored() > energyStorage.getMaxEnergyStored()) {
            energyStorage.setStored(energyStorage.getMaxEnergyStored());
        }

        return result;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void update(Level world) {
        super.update(world);

        List<Destination> destinations = graph.getDestinations(DestinationType.ENERGY_STORAGE);

        if (!destinations.isEmpty()) {
            if (energyStorage.getEnergyStored() <= 0) {
                return;
            }

            for (Destination destination : destinations) {
                BlockEntity tile = destination.getConnectedPipe().getWorld().getBlockEntity(destination.getReceiver());
                if (tile == null) {
                    continue;
                }

                IEnergyStorage handler = tile.getCapability(CapabilityEnergy.ENERGY, destination.getIncomingDirection().getOpposite()).orElse(null);
                if (handler == null) {
                    continue;
                }

                if (!handler.canReceive()) {
                    continue;
                }

                int toOffer = Math.min(pipeType.getTransferRate(), energyStorage.getEnergyStored());
                if (toOffer <= 0) {
                    break;
                }

                toOffer = energyStorage.extractEnergy(toOffer, false);
                if (toOffer <= 0) {
                    break;
                }

                int accepted = handler.receiveEnergy(toOffer, false);

                int remainder = toOffer - accepted;
                if (remainder > 0) {
                    energyStorage.receiveEnergy(remainder, false);
                }
            }
        }
    }

    @Override
    public void onMergedWith(Network mainNetwork) {
        ((EnergyNetwork) mainNetwork).energyStorage.receiveEnergy(energyStorage.getEnergyStored(), false);
    }

    @Override
    public ResourceLocation getType() {
        return pipeType.getNetworkType();
    }

    public EnergyPipeType getPipeType() {
        return pipeType;
    }
}
