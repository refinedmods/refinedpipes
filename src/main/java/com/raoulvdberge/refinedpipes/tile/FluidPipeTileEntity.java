package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipe;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidPipeTileEntity extends PipeTileEntity {
    private final FluidPipeType type;

    public FluidPipeTileEntity(FluidPipeType type) {
        super(type.getTileType());

        this.type = type;
    }

    @Override
    protected Pipe createPipe(World world, BlockPos pos) {
        return new FluidPipe(world, pos, type);
    }

    @Override
    public void tick() {

    }
}
