package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface AttachmentManager {
    boolean[] getState();

    boolean hasAttachment(Direction dir);

    @Nullable
    INamedContainerProvider getContainerProvider(Direction dir);

    @Nonnull
    ItemStack getPickBlock(Direction dir);

    Map<Direction, ResourceLocation> getAttachmentsPerDirection();

    void writeUpdate(CompoundNBT tag);

    void readUpdate(CompoundNBT tag);
}
