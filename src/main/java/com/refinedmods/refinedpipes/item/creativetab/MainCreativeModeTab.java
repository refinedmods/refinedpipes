package com.refinedmods.refinedpipes.item.creativetab;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.RefinedPipesItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MainCreativeModeTab extends CreativeModeTab {
    public MainCreativeModeTab() {
        super(RefinedPipes.ID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RefinedPipesItems.BASIC_ITEM_PIPE);
    }
}
