package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.ItemPipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface AttachmentType {
    ResourceLocation getModelLocation();

    void update(World world, Network network, ItemPipe pipe, Attachment attachment);

    ResourceLocation getId();

    ResourceLocation getItemId();

    ItemStack toStack();

    Attachment createFromNbt(CompoundNBT tag);

    Attachment createNew(Direction dir);
}
