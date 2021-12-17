package com.refinedmods.refinedpipes.item;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.pipe.attachment.AttachmentFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        type.addInformation(tooltip);
    }

    public AttachmentFactory getFactory() {
        return type;
    }
}
