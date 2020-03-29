package com.raoulvdberge.refinedpipes.item;

import com.raoulvdberge.refinedpipes.block.ItemPipeBlock;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipeType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
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
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("misc.refinedpipes.tier", new TranslationTextComponent("enchantment.level." + type.getTier())).setStyle(new Style().setColor(TextFormatting.YELLOW)));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.item_pipe.speed",
            new StringTextComponent("" + type.getSpeedComparedToBasicTier()).setStyle(new Style().setColor(TextFormatting.WHITE))
        ).setStyle(new Style().setColor(TextFormatting.GRAY)));
    }
}
