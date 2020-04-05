package com.raoulvdberge.refinedpipes.network.fluid;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.DestinationType;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipe;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Set;

public class FluidNetwork extends Network {
    private final FluidTank fluidTank = new FluidTank(FluidAttributes.BUCKET_VOLUME);

    private final FluidPipeType pipeType;

    public FluidNetwork(BlockPos originPos, String id, FluidPipeType pipeType) {
        super(originPos, id);

        this.pipeType = pipeType;
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

        if (fluidTank.getFluid().isEmpty() || destinations.isEmpty()) {
            return;
        }

        for (Destination destination : destinations) {
            TileEntity tile = destination.getConnectedPipe().getWorld().getTileEntity(destination.getReceiver());
            if (tile == null) {
                continue;
            }

            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, destination.getIncomingDirection().getOpposite()).orElse(null);
            if (handler == null) {
                continue;
            }

            int toOfferAmount = Math.min(pipeType.getTransferRate(), fluidTank.getFluidAmount());
            if (toOfferAmount <= 0) {
                break;
            }

            FluidStack toOffer = fluidTank.drain(toOfferAmount, IFluidHandler.FluidAction.EXECUTE);
            if (toOffer.isEmpty()) {
                break;
            }

            int accepted = handler.fill(toOffer, IFluidHandler.FluidAction.EXECUTE);

            int remainder = toOffer.getAmount() - accepted;
            if (remainder > 0) {
                FluidStack remainderStack = toOffer.copy();
                remainderStack.setAmount(remainder);

                fluidTank.fill(remainderStack, IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    @Override
    public void onMergedWith(Network mainNetwork) {
        ((FluidNetwork) mainNetwork).getFluidTank().fill(fluidTank.getFluid(), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public ResourceLocation getType() {
        return pipeType.getNetworkType();
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.put("tank", fluidTank.writeToNBT(new CompoundNBT()));

        return super.writeToNbt(tag);
    }
}
