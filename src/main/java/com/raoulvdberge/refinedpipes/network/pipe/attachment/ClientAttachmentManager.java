package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ClientAttachmentManager implements AttachmentManager {
    private final Map<Direction, AttachmentType> attachments = new HashMap<>();
    private final boolean[] attachmentState = new boolean[Direction.values().length];

    @Override
    public boolean[] getState() {
        return attachmentState;
    }

    @Override
    public boolean hasAttachment(Direction dir) {
        return attachments.containsKey(dir);
    }

    @Nullable
    @Override
    public AttachmentType getAttachmentType(Direction dir) {
        return attachments.get(dir);
    }

    @Override
    public Map<Direction, AttachmentType> getAttachmentsPerDirection() {
        return attachments;
    }

    @Override
    public void writeUpdate(CompoundNBT tag) {
        throw new RuntimeException("Client doesn't write updates");
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        this.attachments.clear();
        for (Direction dir : Direction.values()) {
            String key = "attch_" + dir.ordinal();

            if (tag.contains(key)) {
                attachments.put(dir, AttachmentRegistry.INSTANCE.getType(new ResourceLocation(tag.getString(key))));
                attachmentState[dir.ordinal()] = true;
            } else {
                attachmentState[dir.ordinal()] = false;
            }
        }
    }
}
