package com.refinedmods.refinedpipes.item;

import com.refinedmods.refinedpipes.block.FluidPipeBlock;
import com.refinedmods.refinedpipes.network.pipe.fluid.FluidPipeType;
import com.refinedmods.refinedpipes.util.StringUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class FluidPipeBlockItem extends BaseBlockItem {
    private final FluidPipeType type;

    public FluidPipeBlockItem(FluidPipeBlock block) {
        super(block);

        this.type = block.getType();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        tooltip.add(new TranslatableComponent("misc.refinedpipes.tier", new TranslatableComponent("enchantment.level." + type.getTier())).withStyle(ChatFormatting.YELLOW));

        tooltip.add(new TranslatableComponent(
            "tooltip.refinedpipes.fluid_pipe.capacity",
            new TextComponent(StringUtil.formatNumber(type.getCapacity()) + " mB").withStyle(ChatFormatting.WHITE)
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(new TranslatableComponent(
            "tooltip.refinedpipes.fluid_pipe.transfer_rate",
            new TextComponent(StringUtil.formatNumber(type.getTransferRate()) + " mB/t").withStyle(ChatFormatting.WHITE)
        ).withStyle(ChatFormatting.GRAY));
    }
}
