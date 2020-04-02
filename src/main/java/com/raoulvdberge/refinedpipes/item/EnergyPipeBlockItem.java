package com.raoulvdberge.refinedpipes.item;

import com.raoulvdberge.refinedpipes.block.EnergyPipeBlock;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipeType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
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
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("misc.refinedpipes.tier", new TranslationTextComponent("enchantment.level." + type.getTier())).setStyle(new Style().setColor(TextFormatting.YELLOW)));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.energy_pipe.capacity",
            new StringTextComponent(type.getCapacity() + " FE").setStyle(new Style().setColor(TextFormatting.WHITE))
        ).setStyle(new Style().setColor(TextFormatting.GRAY)));
    }
}
