package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.fluid.FluidPipe;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidPipeTileEntity extends PipeTileEntity {
    public FluidPipeTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    protected Pipe createPipe(World world, BlockPos pos) {
        return new FluidPipe(world, pos);
    }

    @Override
    public void tick() {

    }
}
