package com.raoulvdberge.refinedpipes.item.group;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MainItemGroup extends ItemGroup {
    public MainItemGroup() {
        super(RefinedPipes.ID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(RefinedPipesItems.BASIC_PIPE);
    }
}
