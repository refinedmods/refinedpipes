package com.refinedmods.refinedpipes.item;

import com.refinedmods.refinedpipes.block.FluidPipeBlock;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import com.refinedmods.refinedpipes.util.StringUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class FluidPipeBlockItem extends BaseBlockItem {
    private final FluidPipeType type;

    public FluidPipeBlockItem(FluidPipeBlock block) {
        super(block);

        this.type = block.getType();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("misc.refinedpipes.tier", new TranslationTextComponent("enchantment.level." + type.getTier())).setStyle(new Style().setColor(TextFormatting.YELLOW)));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.fluid_pipe.capacity",
            new StringTextComponent(StringUtil.formatNumber(type.getCapacity()) + " mB").setStyle(new Style().setColor(TextFormatting.WHITE))
        ).setStyle(new Style().setColor(TextFormatting.GRAY)));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.fluid_pipe.transfer_rate",
            new StringTextComponent(StringUtil.formatNumber(type.getTransferRate()) + " mB/t").setStyle(new Style().setColor(TextFormatting.WHITE))
        ).setStyle(new Style().setColor(TextFormatting.GRAY)));
    }
}
