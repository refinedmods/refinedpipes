package com.raoulvdberge.refinedpipes.network;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class NetworkRegistry {
    public static final NetworkRegistry INSTANCE = new NetworkRegistry();

    private final Map<ResourceLocation, NetworkFactory> factories = new HashMap<>();

    private NetworkRegistry() {
    }

    public void addFactory(ResourceLocation type, NetworkFactory factory) {
        factories.put(type, factory);
    }

    @Nullable
    public NetworkFactory getFactory(ResourceLocation type) {
        return factories.get(type);
    }
}
