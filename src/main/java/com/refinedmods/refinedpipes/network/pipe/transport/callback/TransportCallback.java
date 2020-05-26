package com.refinedmods.refinedpipes.network.pipe.transport.callback;

import com.refinedmods.refinedpipes.network.Network;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface TransportCallback {
    void call(Network network, World world, BlockPos currentPos, TransportCallback cancelCallback);

    ResourceLocation getId();

    CompoundNBT writeToNbt(CompoundNBT tag);
}
