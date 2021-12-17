package com.refinedmods.refinedpipes.item;

import com.refinedmods.refinedpipes.block.ItemPipeBlock;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPipeBlockItem extends BaseBlockItem {
    private final ItemPipeType type;

    public ItemPipeBlockItem(ItemPipeBlock block) {
        super(block);

        this.type = block.getType();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(new TranslatableComponent("misc.refinedpipes.tier", new TranslatableComponent("enchantment.level." + type.getTier())).withStyle(ChatFormatting.YELLOW));

        tooltip.add(new TranslatableComponent(
            "tooltip.refinedpipes.item_pipe.speed",
            new TextComponent(type.getSpeedComparedToBasicTier() + "%").withStyle(ChatFormatting.WHITE)
        ).withStyle(ChatFormatting.GRAY));
    }
}
