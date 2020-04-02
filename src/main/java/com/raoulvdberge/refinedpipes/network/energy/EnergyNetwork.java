package com.raoulvdberge.refinedpipes.network.energy;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.DestinationType;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Set;

public class EnergyNetwork extends Network {
    public static final ResourceLocation TYPE = new ResourceLocation(RefinedPipes.ID, "energy");

    private final EnergyStorage energyStorage = new EnergyStorage(0);

    public EnergyNetwork(BlockPos originPos, String id) {
        super(originPos, id);
    }

    @Override
    public NetworkGraphScannerResult scanGraph(World world, BlockPos pos) {
        NetworkGraphScannerResult result = super.scanGraph(world, pos);

        energyStorage.setCapacity(
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
            int toDistribute = (int) Math.floor((float) energyStorage.getEnergyStored() / (float) destinations.size());

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

                int energyDrained = energyStorage.extractEnergy(toDistribute, false);
                if (energyDrained == 0) {
                    continue;
                }

                int energyReceived = handler.receiveEnergy(energyDrained, false);

                int remainder = energyDrained - energyReceived;
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
        return TYPE;
    }
}
