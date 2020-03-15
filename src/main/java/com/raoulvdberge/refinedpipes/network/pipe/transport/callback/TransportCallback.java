package com.raoulvdberge.refinedpipes.network.pipe.transport.callback;

import com.raoulvdberge.refinedpipes.network.Network;
import net.minecraft.world.World;

// TODO serialize
@FunctionalInterface
public interface TransportCallback {
    void call(Network network, World world, TransportCallback cancelCallback);
}
