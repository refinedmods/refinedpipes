package com.raoulvdberge.refinedpipes.item;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.AttachmentType;
import net.minecraft.item.Item;

public class AttachmentItem extends Item {
    public AttachmentItem() {
        super(new Item.Properties().group(RefinedPipes.MAIN_GROUP));

        this.setRegistryName(RefinedPipes.ID, "attachment");
    }

    public AttachmentType getType() {
        return AttachmentType.NORMAL;
    }
}
