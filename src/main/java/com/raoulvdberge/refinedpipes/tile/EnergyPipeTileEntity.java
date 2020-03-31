package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipe;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyPipeTileEntity extends PipeTileEntity {
    private final EnergyPipeType type;

    public EnergyPipeTileEntity(EnergyPipeType type) {
        super(type.getTileType());

        this.type = type;
    }

    @Override
    protected Pipe createPipe(World world, BlockPos pos) {
        return new EnergyPipe(world, pos, type);
    }
}
