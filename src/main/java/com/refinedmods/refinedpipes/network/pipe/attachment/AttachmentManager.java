package com.refinedmods.refinedpipes.network.pipe.attachment;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AttachmentManager {
    ResourceLocation[] getState();

    boolean hasAttachment(Direction dir);

    void openAttachmentContainer(Direction dir, ServerPlayer player);

    @Nonnull
    ItemStack getPickBlock(Direction dir);

    @Nullable
    Attachment getAttachment(Direction dir);

    void writeUpdate(CompoundTag tag);

    void readUpdate(@Nullable CompoundTag tag);
}
