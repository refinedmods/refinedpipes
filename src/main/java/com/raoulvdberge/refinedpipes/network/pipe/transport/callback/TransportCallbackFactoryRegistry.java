package com.raoulvdberge.refinedpipes.network.pipe.transport.callback;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TransportCallbackFactoryRegistry {
    public static final TransportCallbackFactoryRegistry INSTANCE = new TransportCallbackFactoryRegistry();

    private final Map<ResourceLocation, TransportCallbackFactory> factories = new HashMap<>();

    private TransportCallbackFactoryRegistry() {
    }

    public void addFactory(ResourceLocation id, TransportCallbackFactory factory) {
        factories.put(id, factory);
    }

    @Nullable
    public TransportCallbackFactory getFactory(ResourceLocation id) {
        return factories.get(id);
    }
}
