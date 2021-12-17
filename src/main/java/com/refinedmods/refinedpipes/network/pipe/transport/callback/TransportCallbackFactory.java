package com.refinedmods.refinedpipes.network.pipe.transport.callback;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public interface TransportCallbackFactory {
    @Nullable
    TransportCallback create(CompoundTag tag);
}
