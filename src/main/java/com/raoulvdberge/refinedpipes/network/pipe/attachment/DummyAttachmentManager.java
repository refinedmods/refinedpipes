package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class DummyAttachmentManager implements AttachmentManager {
    private static final boolean[] STATE = new boolean[Direction.values().length];

    public static final DummyAttachmentManager INSTANCE = new DummyAttachmentManager();

    private DummyAttachmentManager() {
    }

    @Override
    public boolean[] getState() {
        return STATE;
    }

    @Override
    public boolean hasAttachment(Direction dir) {
        return false;
    }

    @Nullable
    @Override
    public AttachmentType getAttachmentType(Direction dir) {
        return null;
    }

    @Override
    public Map<Direction, AttachmentType> getAttachmentsPerDirection() {
        return Collections.emptyMap();
    }

    @Override
    public void writeUpdate(CompoundNBT tag) {

    }

    @Override
    public void readUpdate(CompoundNBT tag) {

    }
}
