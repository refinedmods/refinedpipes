package com.refinedmods.refinedpipes.item;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentFactory;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class AttachmentItem extends Item {
    private final AttachmentFactory type;

    public AttachmentItem(AttachmentFactory type) {
        super(new Item.Properties().tab(RefinedPipes.MAIN_GROUP));

        this.type = type;

        this.setRegistryName(type.getItemId());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        type.addInformation(tooltip);
    }

    public AttachmentFactory getFactory() {
        return type;
    }
}
