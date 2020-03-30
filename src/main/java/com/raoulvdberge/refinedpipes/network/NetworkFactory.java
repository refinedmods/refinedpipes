package com.raoulvdberge.refinedpipes.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public interface NetworkFactory {
    Network create(BlockPos pos);

    Network create(CompoundNBT tag);
}
