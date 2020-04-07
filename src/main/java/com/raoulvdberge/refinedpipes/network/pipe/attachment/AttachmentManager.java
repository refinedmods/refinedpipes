package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

public interface AttachmentManager {
    boolean[] getState();

    boolean hasAttachment(Direction dir);

    @Nonnull
    ItemStack getPickBlock(Direction dir);

    Map<Direction, ResourceLocation> getAttachmentsPerDirection();

    void writeUpdate(CompoundNBT tag);

    void readUpdate(CompoundNBT tag);
}
