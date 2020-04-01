package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Map;

public interface AttachmentManager {
    boolean[] getState();

    boolean hasAttachment(Direction dir);

    @Nullable
    AttachmentType getAttachmentType(Direction dir);

    Map<Direction, AttachmentType> getAttachmentsPerDirection();

    void writeUpdate(CompoundNBT tag);

    void readUpdate(CompoundNBT tag);
}
