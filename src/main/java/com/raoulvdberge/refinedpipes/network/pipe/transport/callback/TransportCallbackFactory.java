package com.raoulvdberge.refinedpipes.network.pipe.transport.callback;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public interface TransportCallbackFactory {
    @Nullable
    TransportCallback create(CompoundNBT tag);
}
