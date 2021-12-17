package com.refinedmods.refinedpipes.network.pipe.attachment;

import com.refinedmods.refinedpipes.network.pipe.Pipe;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface AttachmentFactory {
    Attachment createFromNbt(Pipe pipe, CompoundTag tag);

    Attachment create(Pipe pipe, Direction dir);

    ResourceLocation getItemId();

    ResourceLocation getId();

    ResourceLocation getModelLocation();

    void addInformation(List<Component> tooltip);

    boolean canPlaceOnPipe(Block pipe);
}
