package com.refinedmods.refinedpipes.item.group;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.RefinedPipesItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MainItemGroup extends CreativeModeTab {
    public MainItemGroup() {
        super(RefinedPipes.ID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RefinedPipesItems.BASIC_ITEM_PIPE);
    }
}
