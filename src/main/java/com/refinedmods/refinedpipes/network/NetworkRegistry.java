package com.refinedmods.refinedpipes.network;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class NetworkRegistry {
    public static final NetworkRegistry INSTANCE = new NetworkRegistry();
    private static final Logger LOGGER = LogManager.getLogger(NetworkRegistry.class);
    private final Map<ResourceLocation, NetworkFactory> factories = new HashMap<>();

    private NetworkRegistry() {
    }

    public void addFactory(ResourceLocation type, NetworkFactory factory) {
        if (factories.containsKey(type)) {
            throw new RuntimeException("Cannot register duplicate network type " + type.toString());
        }

        LOGGER.debug("Registering network factory {}", type.toString());

        factories.put(type, factory);
    }

    @Nullable
    public NetworkFactory getFactory(ResourceLocation type) {
        return factories.get(type);
    }
}
