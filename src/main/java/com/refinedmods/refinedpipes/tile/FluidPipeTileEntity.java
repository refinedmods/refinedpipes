package com.refinedmods.refinedpipes.tile;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.fluid.FluidNetwork;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipe;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class FluidPipeTileEntity extends PipeTileEntity {
    private final FluidPipeType type;

    private FluidStack fluid = FluidStack.EMPTY;
    private float fullness = 0;
    private float renderFullness = 0;

    public FluidPipeTileEntity(FluidPipeType type) {
        super(type.getTileType());

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
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        Pipe pipe = NetworkManager.get(world).getPipe(pos);
        if (pipe instanceof FluidPipe && pipe.getNetwork() != null) {
            tag.put("fluid", ((FluidNetwork) pipe.getNetwork()).getFluidTank().getFluid().writeToNBT(new CompoundNBT()));
            tag.putFloat("fullness", ((FluidPipe) pipe).getFullness());
        }

        return super.writeUpdate(tag);
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        if (tag.contains("fluid")) {
            fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("fluid"));
        }

        if (tag.contains("fullness")) {
            fullness = tag.getFloat("fullness");
            renderFullness = fullness;
        }

        super.readUpdate(tag);
    }

    public void setFullness(float fullness) {
        this.fullness = fullness;
    }

    @Override
    protected Pipe createPipe(World world, BlockPos pos) {
        return new FluidPipe(world, pos, type);
    }
}
