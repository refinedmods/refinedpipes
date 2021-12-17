package com.refinedmods.refinedpipes.network.pipe.attachment;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DummyAttachmentManager implements AttachmentManager {
    public static final DummyAttachmentManager INSTANCE = new DummyAttachmentManager();
    private static final ResourceLocation[] STATE = new ResourceLocation[Direction.values().length];

    private DummyAttachmentManager() {
    }

    @Override
    public ResourceLocation[] getState() {
        return STATE;
    }

    @Override
    public boolean hasAttachment(Direction dir) {
        return false;
    }

    @Override
    public void openAttachmentContainer(Direction dir, ServerPlayer player) {

    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(Direction dir) {
        return ItemStack.EMPTY;
    }

    @Override
    @Nullable
    public Attachment getAttachment(Direction dir) {
        return null;
    }

    @Override
    public void writeUpdate(CompoundTag tag) {

    }

    @Override
    public void readUpdate(@Nullable CompoundTag tag) {

    }
}
