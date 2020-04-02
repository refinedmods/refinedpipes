package com.raoulvdberge.refinedpipes.network.fluid;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.DestinationType;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Set;

public class FluidNetwork extends Network {
    public static final ResourceLocation TYPE = new ResourceLocation(RefinedPipes.ID, "fluid");

    private final FluidTank fluidTank = new FluidTank(FluidAttributes.BUCKET_VOLUME);

    public FluidNetwork(BlockPos originPos, String id) {
        super(originPos, id);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public NetworkGraphScannerResult scanGraph(World world, BlockPos pos) {
        NetworkGraphScannerResult result = super.scanGraph(world, pos);

        fluidTank.setCapacity(
            result.getFoundPipes()
                .stream()
                .filter(p -> p instanceof FluidPipe)
                .mapToInt(p -> ((FluidPipe) p).getType().getCapacity())
                .sum()
        );

        if (fluidTank.getFluidAmount() > fluidTank.getCapacity()) {
            fluidTank.getFluid().setAmount(fluidTank.getCapacity());
        }

        return result;
    }

    @Override
    public void update(World world) {
        super.update(world);

        Set<Destination> destinations = graph.getDestinations(DestinationType.FLUID_HANDLER);

        if (!fluidTank.getFluid().isEmpty() && !destinations.isEmpty()) {
            int toDistribute = (int) Math.floor((float) getThroughput() / (float) destinations.size());

            for (Destination destination : destinations) {
                TileEntity tile = destination.getConnectedPipe().getWorld().getTileEntity(destination.getReceiver());
                if (tile == null) {
                    continue;
                }

                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, destination.getIncomingDirection().getOpposite()).orElse(null);
                if (handler == null) {
                    continue;
                }

                FluidStack drained = fluidTank.drain(toDistribute, IFluidHandler.FluidAction.EXECUTE);
                if (drained.isEmpty()) {
                    continue;
                }

                int filled = handler.fill(drained, IFluidHandler.FluidAction.EXECUTE);

                int remainder = drained.getAmount() - filled;
                if (remainder > 0) {
                    drained = drained.copy();
                    drained.setAmount(remainder);

                    fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    @Override
    public void onMergedWith(Network mainNetwork) {
        ((FluidNetwork) mainNetwork).getFluidTank().fill(fluidTank.getFluid(), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    private int getThroughput() {
        int viscosity = fluidTank.getFluid().getFluid().getAttributes().getViscosity(fluidTank.getFluid());
        viscosity = Math.max(100, viscosity);

        return MathHelper.clamp(120000 / viscosity, 80, 600);
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.put("tank", fluidTank.writeToNBT(new CompoundNBT()));

        return super.writeToNbt(tag);
    }
}
