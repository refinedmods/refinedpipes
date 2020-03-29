package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipe;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class FluidPipeTileEntity extends PipeTileEntity {
    private final FluidPipeType type;

    private FluidStack fluid = FluidStack.EMPTY;
    private float fullness = 0;

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

    public float getFullness() {
        return fullness;
    }

    public void setFullness(float fullness) {
        this.fullness = fullness;
    }

    @Override
    protected Pipe createPipe(World world, BlockPos pos) {
        return new FluidPipe(world, pos, type);
    }

    @Override
    public void tick() {

    }
}
