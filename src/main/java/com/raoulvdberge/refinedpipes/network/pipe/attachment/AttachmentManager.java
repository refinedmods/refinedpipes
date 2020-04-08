package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AttachmentManager {
    ResourceLocation[] getState();

    boolean hasAttachment(Direction dir);

    void openAttachmentContainer(Direction dir, ServerPlayerEntity player);

    @Nonnull
    ItemStack getPickBlock(Direction dir);

    @Nullable
    Attachment getAttachment(Direction dir);

    void writeUpdate(CompoundNBT tag);

    void readUpdate(CompoundNBT tag);
}
