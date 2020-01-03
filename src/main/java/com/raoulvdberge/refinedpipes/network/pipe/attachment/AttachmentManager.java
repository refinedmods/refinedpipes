package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttachmentManager {
    private final Map<Direction, Attachment> attachments = new HashMap<>();

    public boolean hasAttachment(Direction dir) {
        return attachments.containsKey(dir);
    }

    public void removeAttachment(Direction dir) {
        attachments.remove(dir);
    }

    @Nullable
    public Attachment getAttachment(Direction dir) {
        return attachments.get(dir);
    }

    public Collection<Attachment> getAttachments() {
        return attachments.values();
    }

    public void setAttachment(Direction dir, AttachmentType type) {
        attachments.put(dir, type.createNew(dir));
    }

    public void setAttachment(Direction dir, Attachment attachment) {
        attachments.put(dir, attachment);
    }
}
