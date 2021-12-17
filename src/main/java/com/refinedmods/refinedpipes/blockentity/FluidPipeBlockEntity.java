package com.refinedmods.refinedpipes.blockentity;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.fluid.FluidNetwork;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipe;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidPipeBlockEntity extends PipeBlockEntity {
    private final FluidPipeType type;

    private FluidStack fluid = FluidStack.EMPTY;
    private float fullness = 0;
    private float renderFullness = 0;

    public FluidPipeBlockEntity(BlockPos pos, BlockState state, FluidPipeType type) {
        super(type.getBlockEntityType(), pos, state);

        this.type = type;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public void setFluid(FluidStack fluid) {
        this.fluid = fluid;
    }

    public float updateAndGetRenderFullness(float partialTicks) {
        float step = partialTicks * 0.05F;

        if (renderFullness > fullness) {
            renderFullness -= step;

            if (renderFullness < fullness) {
                renderFullness = fullness;
            }
        } else if (renderFullness < fullness) {
            renderFullness += step;

            if (renderFullness > fullness) {
                renderFullness = fullness;
            }
        }

        return renderFullness;
    }

    @Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        Pipe pipe = NetworkManager.get(level).getPipe(worldPosition);
        if (pipe instanceof FluidPipe && pipe.getNetwork() != null) {
            tag.put("fluid", ((FluidNetwork) pipe.getNetwork()).getFluidTank().getFluid().writeToNBT(new CompoundTag()));
            tag.putFloat("fullness", ((FluidPipe) pipe).getFullness());
        }

        return super.writeUpdate(tag);
    }

    @Override
    public void readUpdate(@Nullable CompoundTag tag) {
        if (tag != null && tag.contains("fluid")) {
            fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("fluid"));
        } else {
            fluid = FluidStack.EMPTY;
        }

        if (tag != null && tag.contains("fullness")) {
            fullness = tag.getFloat("fullness");
            renderFullness = fullness;
        } else {
            fullness = 0;
            renderFullness = 0;
        }

        super.readUpdate(tag);
    }

    public void setFullness(float fullness) {
        this.fullness = fullness;
    }

    @Override
    protected Pipe createPipe(Level level, BlockPos pos) {
        return new FluidPipe(level, pos, type);
    }
}
