package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.entity.player.ServerPlayerEntity;
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

    @Override
    public void openAttachmentContainer(Direction dir, ServerPlayerEntity player) {

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
    @Nullable
    public Attachment getAttachment(Direction dir) {
        return null;
    }

    @Override
    public void writeUpdate(CompoundNBT tag) {

    }

    @Override
    public void readUpdate(CompoundNBT tag) {

    }
}
