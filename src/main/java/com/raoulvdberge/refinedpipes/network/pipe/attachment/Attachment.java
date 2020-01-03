package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class Attachment {
    private final AttachmentType type;
    private final Direction direction;

    public Attachment(AttachmentType type, Direction direction) {
        this.type = type;
        this.direction = direction;
    }

    public AttachmentType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putInt("dir", direction.ordinal());

        return tag;
    }
}
