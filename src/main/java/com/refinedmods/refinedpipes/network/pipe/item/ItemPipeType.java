package com.refinedmods.refinedpipes.network.pipe.item;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.RefinedPipesBlockEntities;
import com.refinedmods.refinedpipes.blockentity.ItemPipeBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

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

    public int getSpeedComparedToBasicTier() {
        int mySpeed = this == BASIC ? getMaxTicksInPipe() :
            (this == IMPROVED ? BASIC.getMaxTicksInPipe() + getMaxTicksInPipe() :
                (this == ADVANCED ? BASIC.getMaxTicksInPipe() + IMPROVED.getMaxTicksInPipe() + getMaxTicksInPipe() :
                    0));

        int speedOfBasicTier = BASIC.getMaxTicksInPipe();

        return (int) ((float) mySpeed / (float) speedOfBasicTier * 100F);
    }

    public BlockEntityType<ItemPipeBlockEntity> getBlockEntityType() {
        switch (this) {
            case BASIC:
                return RefinedPipesBlockEntities.BASIC_ITEM_PIPE;
            case IMPROVED:
                return RefinedPipesBlockEntities.IMPROVED_ITEM_PIPE;
            case ADVANCED:
                return RefinedPipesBlockEntities.ADVANCED_ITEM_PIPE;
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
