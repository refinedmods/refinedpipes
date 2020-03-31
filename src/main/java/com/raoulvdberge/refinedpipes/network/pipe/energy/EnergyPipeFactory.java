package com.raoulvdberge.refinedpipes.network.pipe.energy;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.PipeFactory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyPipeFactory implements PipeFactory {
    @Override
    public Pipe createFromNbt(World world, CompoundNBT tag) {
        BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));

        EnergyPipeType pipeType = EnergyPipeType.values()[tag.getInt("type")];

        EnergyPipe pipe = new EnergyPipe(world, pos, pipeType);

        pipe.getAttachmentManager().readFromNbt(tag);

        return pipe;
    }
}
