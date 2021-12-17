package com.refinedmods.refinedpipes.network.pipe.transport.callback;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TransportCallbackFactoryRegistry {
    public static final TransportCallbackFactoryRegistry INSTANCE = new TransportCallbackFactoryRegistry();

    private final Map<ResourceLocation, TransportCallbackFactory> factories = new HashMap<>();

    private TransportCallbackFactoryRegistry() {
    }

    public void addFactory(ResourceLocation id, TransportCallbackFactory factory) {
        if (factories.containsKey(id)) {
            throw new RuntimeException("Cannot register duplicate transport callback factory " + id.toString());
        }

        factories.put(id, factory);
    }

    @Nullable
    public TransportCallbackFactory getFactory(ResourceLocation id) {
        return factories.get(id);
    }
}
