package com.refinedmods.refinedpipes.network.pipe;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public interface PipeFactory {
    Pipe createFromNbt(World world, CompoundNBT tag);
}
