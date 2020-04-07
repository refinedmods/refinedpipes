package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
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
    public INamedContainerProvider getContainerProvider(Direction dir) {
        return null;
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(Direction dir) {
        return ItemStack.EMPTY;
    }

    @Override
    public Map<Direction, ResourceLocation> getAttachmentsPerDirection() {
        return Collections.emptyMap();
    }

    @Override
    public void writeUpdate(CompoundNBT tag) {

    }

    @Override
    public void readUpdate(CompoundNBT tag) {

    }
}
