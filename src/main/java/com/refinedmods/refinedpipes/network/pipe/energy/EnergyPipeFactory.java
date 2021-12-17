package com.refinedmods.refinedpipes.network.pipe.energy;

import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.PipeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class EnergyPipeFactory implements PipeFactory {
    @Override
    public Pipe createFromNbt(Level world, CompoundTag tag) {
        BlockPos pos = BlockPos.of(tag.getLong("pos"));

        EnergyPipeType pipeType = EnergyPipeType.values()[tag.getInt("type")];

        EnergyPipe pipe = new EnergyPipe(world, pos, pipeType);

        pipe.getAttachmentManager().readFromNbt(tag);

        return pipe;
    }
}
