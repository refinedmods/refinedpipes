package com.refinedmods.refinedpipes.item.group;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.RefinedPipesItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MainItemGroup extends ItemGroup {
    public MainItemGroup() {
        super(RefinedPipes.ID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RefinedPipesItems.BASIC_ITEM_PIPE);
    }
}
