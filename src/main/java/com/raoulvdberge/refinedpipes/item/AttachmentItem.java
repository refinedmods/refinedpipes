package com.raoulvdberge.refinedpipes.item;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import net.minecraft.item.Item;

public class AttachmentItem extends Item {
    private final AttachmentType type;

    public AttachmentItem(AttachmentType type) {
        super(new Item.Properties().group(RefinedPipes.MAIN_GROUP));

        this.type = type;

        this.setRegistryName(type.getItemId());
    }

    public AttachmentType getType() {
        return type;
    }
}
