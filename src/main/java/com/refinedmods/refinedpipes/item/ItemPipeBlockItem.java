package com.refinedmods.refinedpipes.item;

import com.refinedmods.refinedpipes.block.ItemPipeBlock;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipeType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPipeBlockItem extends BaseBlockItem {
    private final ItemPipeType type;

    public ItemPipeBlockItem(ItemPipeBlock block) {
        super(block);

        this.type = block.getType();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("misc.refinedpipes.tier", new TranslationTextComponent("enchantment.level." + type.getTier())).withStyle(TextFormatting.YELLOW));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.item_pipe.speed",
            new StringTextComponent(type.getSpeedComparedToBasicTier() + "%").withStyle(TextFormatting.WHITE)
        ).withStyle(TextFormatting.GRAY));
    }
}
