package com.raoulvdberge.refinedpipes.item;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class AttachmentItem extends Item {
    private final AttachmentType type;

    public AttachmentItem(AttachmentType type) {
        super(new Item.Properties().group(RefinedPipes.MAIN_GROUP));

        this.type = type;

        this.setRegistryName(type.getItemId());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        type.addInformation(tooltip);
    }

    public AttachmentType getType() {
        return type;
    }
}
