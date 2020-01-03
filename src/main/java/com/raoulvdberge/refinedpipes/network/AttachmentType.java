package com.raoulvdberge.refinedpipes.network;

import net.minecraft.util.IStringSerializable;

public enum AttachmentType implements IStringSerializable {
    NORMAL("normal");

    private String name;

    AttachmentType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
