package com.raoulvdberge.refinedpipes.network.pipe;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.tile.ItemPipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public enum ItemPipeType {
    BASIC(1, 30),
    IMPROVED(2, 20),
    ADVANCED(3, 10);

    private final int tier;
    private final int maxTicksInPipe;

    ItemPipeType(int tier, int maxTicksInPipe) {
        this.tier = tier;
        this.maxTicksInPipe = maxTicksInPipe;
    }

    public int getTier() {
        return tier;
    }

    public int getMaxTicksInPipe() {
        return maxTicksInPipe;
    }

    public TileEntityType<ItemPipeTileEntity> getTileType() {
        switch (this) {
            case BASIC:
                return RefinedPipesTileEntities.BASIC_ITEM_PIPE;
            case IMPROVED:
                return RefinedPipesTileEntities.IMPROVED_ITEM_PIPE;
            case ADVANCED:
                return RefinedPipesTileEntities.ADVANCED_ITEM_PIPE;
            default:
                throw new RuntimeException("?");
        }
    }

    public ResourceLocation getId() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "basic_item_pipe");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "improved_item_pipe");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "advanced_item_pipe");
            default:
                throw new RuntimeException("?");
        }
    }
}
