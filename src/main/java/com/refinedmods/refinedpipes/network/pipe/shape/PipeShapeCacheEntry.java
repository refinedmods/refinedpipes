package com.refinedmods.refinedpipes.network.pipe.shape;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Objects;

public class PipeShapeCacheEntry {
    private final BlockState state;
    private final ResourceLocation[] attachmentState;

    public PipeShapeCacheEntry(BlockState state, ResourceLocation[] attachmentState) {
        this.state = state;
        this.attachmentState = attachmentState;
    }

    public BlockState getState() {
        return state;
    }

    public ResourceLocation[] getAttachmentState() {
        return attachmentState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PipeShapeCacheEntry that = (PipeShapeCacheEntry) o;
        return state.equals(that.state) &&
            Arrays.equals(attachmentState, that.attachmentState);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(state);
        result = 31 * result + Arrays.hashCode(attachmentState);
        return result;
    }
}
