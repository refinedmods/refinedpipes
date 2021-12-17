package com.refinedmods.refinedpipes.network.pipe.attachment;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttachmentRegistry {
    public static final AttachmentRegistry INSTANCE = new AttachmentRegistry();

    private final Map<ResourceLocation, AttachmentFactory> factories = new HashMap<>();

    private AttachmentRegistry() {
    }

    public Collection<AttachmentFactory> all() {
        return factories.values();
    }

    public void addFactory(ResourceLocation id, AttachmentFactory type) {
        if (factories.containsKey(id)) {
            throw new RuntimeException("Cannot register duplicate attachment factory " + id.toString());
        }

        factories.put(id, type);
    }

    @Nullable
    public AttachmentFactory getFactory(ResourceLocation id) {
        return factories.get(id);
    }
}
