package com.refinedmods.refinedpipes.network.fluid;

import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.network.graph.NetworkGraphScannerResult;
import com.refinedmods.refinedpipes.network.pipe.Destination;
import com.refinedmods.refinedpipes.network.pipe.DestinationType;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipe;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;

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
    public NetworkGraphScannerResult scanGraph(Level level, BlockPos pos) {
        NetworkGraphScannerResult result = super.scanGraph(level, pos);

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
    public void update(Level level) {
        super.update(level);

        List<Destination> destinations = graph.getDestinations(DestinationType.FLUID_HANDLER);

        if (fluidTank.getFluid().isEmpty() || destinations.isEmpty()) {
            return;
        }

        for (Destination destination : destinations) {
            BlockEntity blockEntity = destination.getConnectedPipe().getLevel().getBlockEntity(destination.getReceiver());
            if (blockEntity == null) {
                continue;
            }

            IFluidHandler handler = blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, destination.getIncomingDirection().getOpposite()).orElse(null);
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
    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.put("tank", fluidTank.writeToNBT(new CompoundTag()));

        return super.writeToNbt(tag);
    }
}
