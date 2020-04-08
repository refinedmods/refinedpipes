package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public interface AttachmentFactory {
    Attachment createFromNbt(Pipe pipe, CompoundNBT tag);

    Attachment create(Pipe pipe, Direction dir);

    ResourceLocation getItemId();

    ResourceLocation getId();

    ResourceLocation getModelLocation();

    void addInformation(List<ITextComponent> tooltip);

    boolean canPlaceOnPipe(Block pipe);
}
