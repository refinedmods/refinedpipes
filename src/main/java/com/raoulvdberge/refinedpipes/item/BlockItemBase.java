package com.raoulvdberge.refinedpipes.item;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BlockItemBase extends BlockItem {
    public BlockItemBase(Block block) {
        super(block, new Item.Properties().group(RefinedPipes.MAIN_GROUP));

        this.setRegistryName(block.getRegistryName());
    }
}
