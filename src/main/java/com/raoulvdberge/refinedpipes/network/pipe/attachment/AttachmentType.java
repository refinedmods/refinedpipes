package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public interface AttachmentType {
    ResourceLocation getModelLocation();

    ResourceLocation getId();

    ResourceLocation getItemId();

    ItemStack toStack();

    Attachment createFromNbt(CompoundNBT tag);

    Attachment createNew(Direction dir);
}
