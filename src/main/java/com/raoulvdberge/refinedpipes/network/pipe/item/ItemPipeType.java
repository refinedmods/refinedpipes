package com.raoulvdberge.refinedpipes.network.pipe.item;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.tile.ItemPipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public enum ItemPipeType {
    BASIC(1),
    IMPROVED(2),
    ADVANCED(3);

    private final int tier;

    ItemPipeType(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public int getMaxTicksInPipe() {
        switch (this) {
            case BASIC:
                return RefinedPipes.SERVER_CONFIG.getBasicItemPipe().getMaxTicks();
            case IMPROVED:
                return RefinedPipes.SERVER_CONFIG.getImprovedItemPipe().getMaxTicks();
            case ADVANCED:
                return RefinedPipes.SERVER_CONFIG.getAdvancedItemPipe().getMaxTicks();
            default:
                throw new RuntimeException("?");
        }
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
