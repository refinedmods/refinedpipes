package com.raoulvdberge.refinedpipes.network;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class AttachmentManager {
    private final Map<Direction, Attachment> attachments = new HashMap<>();

    public AttachmentManager() {
        for (Direction dir : Direction.values()) {
            setAttachment(dir, AttachmentType.NONE);
        }
    }

    public boolean hasAttachment(Direction dir) {
        return getAttachment(dir).getType() != AttachmentType.NONE;
    }

    @Nonnull
    public Attachment getAttachment(Direction dir) {
        return attachments.get(dir);
    }

    public void setAttachment(Direction dir, AttachmentType type) {
        attachments.put(dir, new Attachment(type));
    }
}
