package com.refinedmods.refinedpipes.item;

import com.refinedmods.refinedpipes.RefinedPipes;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BaseBlockItem extends BlockItem {
    public BaseBlockItem(Block block) {
        super(block, new Item.Properties().tab(RefinedPipes.MAIN_GROUP));

        this.setRegistryName(block.getRegistryName());
    }
}
