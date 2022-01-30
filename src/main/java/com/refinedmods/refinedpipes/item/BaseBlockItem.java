package com.refinedmods.refinedpipes.item;

import com.refinedmods.refinedpipes.RefinedPipes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BaseBlockItem extends BlockItem {
    public BaseBlockItem(Block block) {
        super(block, new Item.Properties().tab(RefinedPipes.CREATIVE_MODE_TAB));

        this.setRegistryName(block.getRegistryName());
    }
}
