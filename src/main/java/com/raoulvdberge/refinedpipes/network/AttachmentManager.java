package com.raoulvdberge.refinedpipes.network;

import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class AttachmentManager {
    private final Map<Direction, Attachment> attachments = new HashMap<>();

    public boolean hasAttachment(Direction dir) {
        return attachments.containsKey(dir);
    }

    @Nullable
    public Attachment getAttachment(Direction dir) {
        return attachments.get(dir);
    }

    public void setAttachment(Direction dir, AttachmentType type) {
        attachments.put(dir, new Attachment(type));
    }
}
