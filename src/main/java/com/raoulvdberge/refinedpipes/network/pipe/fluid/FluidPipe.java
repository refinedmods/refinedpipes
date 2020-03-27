package com.raoulvdberge.refinedpipes.network.pipe.fluid;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidPipe extends Pipe {
    public FluidPipe(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public boolean canFormNetworkWith(Pipe otherPipe) {
        return otherPipe instanceof FluidPipe;
    }
}
