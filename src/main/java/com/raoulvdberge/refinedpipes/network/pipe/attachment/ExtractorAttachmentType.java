package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class ExtractorAttachmentType implements AttachmentType {
    private static final ResourceLocation MODEL_LOCATION = new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor_attachment");
    private static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "extractor");
    private static final ResourceLocation ITEM_ID = new ResourceLocation(RefinedPipes.ID, "extractor_attachment");

    @Override
    public ResourceLocation getModelLocation() {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getItemId() {
        return ITEM_ID;
    }

    @Override
    public ItemStack toStack() {
        return new ItemStack(RefinedPipesItems.EXTRACTOR_ATTACHMENT);
    }

    @Override
    public Attachment createFromNbt(CompoundNBT tag) {
        Direction dir = Direction.values()[tag.getInt("dir")];

        return new Attachment(this, dir);
    }

    @Override
    public Attachment createNew(Direction dir) {
        return new Attachment(this, dir);
    }
}
