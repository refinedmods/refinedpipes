package com.refinedmods.refinedpipes.item;

import com.refinedmods.refinedpipes.block.EnergyPipeBlock;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeType;
import com.refinedmods.refinedpipes.util.StringUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EnergyPipeBlockItem extends BaseBlockItem {
    private final EnergyPipeType type;

    public EnergyPipeBlockItem(EnergyPipeBlock block) {
        super(block);

        this.type = block.getType();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("misc.refinedpipes.tier", new TranslationTextComponent("enchantment.level." + type.getTier())).withStyle(TextFormatting.YELLOW));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.energy_pipe.capacity",
            new StringTextComponent(StringUtil.formatNumber(type.getCapacity()) + " FE").withStyle(TextFormatting.WHITE)
        ).withStyle(TextFormatting.GRAY));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.energy_pipe.transfer_rate",
            new StringTextComponent(StringUtil.formatNumber(type.getTransferRate()) + " FE/t").withStyle(TextFormatting.WHITE)
        ).withStyle(TextFormatting.GRAY));
    }
}
