package com.refinedmods.refinedpipes.network.pipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface PipeFactory {
    Pipe createFromNbt(Level level, CompoundTag tag);
}
