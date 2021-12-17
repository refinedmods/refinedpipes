package com.refinedmods.refinedpipes.network.pipe.fluid;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.message.FluidPipeMessage;
import com.refinedmods.refinedpipes.network.fluid.FluidNetwork;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FluidPipe extends Pipe {
    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "fluid");

    private final FluidPipeType type;
    private float lastFullness = 0;

    public FluidPipe(Level level, BlockPos pos, FluidPipeType type) {
        super(level, pos);

        this.type = type;
    }

    @Override
    public void update() {
        super.update();

        float f = getFullness();
        if (Math.abs(lastFullness - f) >= 0.1) {
            lastFullness = f;

            sendFluidPipeUpdate();
        }
    }

    public void sendFluidPipeUpdate() {
        RefinedPipes.NETWORK.sendInArea(level, pos, 32, new FluidPipeMessage(pos, ((FluidNetwork) network).getFluidTank().getFluid(), getFullness()));
    }

    public float getFullness() {
        int cap = ((FluidNetwork) network).getFluidTank().getCapacity();
        int stored = ((FluidNetwork) network).getFluidTank().getFluidAmount();

        return Math.round(((float) stored / (float) cap) * 10.0F) / 10.0F;
    }

    public FluidPipeType getType() {
        return type;
    }

    @Override
    public CompoundTag writeToNbt(CompoundTag tag) {
        tag = super.writeToNbt(tag);

        tag.putInt("type", type.ordinal());

        return tag;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getNetworkType() {
        return type.getNetworkType();
    }
}
