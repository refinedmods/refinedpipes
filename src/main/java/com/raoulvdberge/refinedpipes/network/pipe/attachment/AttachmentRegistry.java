package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttachmentRegistry {
    public static final AttachmentRegistry INSTANCE = new AttachmentRegistry();

    private final Map<ResourceLocation, AttachmentType> types = new HashMap<>();

    private AttachmentRegistry() {
    }

    public Collection<AttachmentType> getTypes() {
        return types.values();
    }

    public void addType(AttachmentType type) {
        types.put(type.getId(), type);
    }

    @Nullable
    public AttachmentType getType(ResourceLocation id) {
        return types.get(id);
    }
}
