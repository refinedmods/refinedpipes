package com.raoulvdberge.refinedpipes.network.energy;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.DestinationType;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipe;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Set;

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
    public NetworkGraphScannerResult scanGraph(World world, BlockPos pos) {
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
    public void update(World world) {
        super.update(world);

        Set<Destination> destinations = graph.getDestinations(DestinationType.ENERGY_STORAGE);

        if (!destinations.isEmpty()) {
            if (energyStorage.getEnergyStored() <= 0) {
                return;
            }

            for (Destination destination : destinations) {
                TileEntity tile = destination.getConnectedPipe().getWorld().getTileEntity(destination.getReceiver());
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
