package com.raoulvdberge.refinedpipes.network.pipe;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public enum PipeType {
    SIMPLE(30),
    BASIC(20),
    IMPROVED(10),
    ADVANCED(5);

    private final int maxTicksInPipe;

    PipeType(int maxTicksInPipe) {
        this.maxTicksInPipe = maxTicksInPipe;
    }

    public int getMaxTicksInPipe() {
        return maxTicksInPipe;
    }

    public TileEntityType<PipeTileEntity> getTileType() {
        switch (this) {
            case SIMPLE:
                return RefinedPipesTileEntities.SIMPLE_PIPE;
            case BASIC:
                return RefinedPipesTileEntities.BASIC_PIPE;
            case IMPROVED:
                return RefinedPipesTileEntities.IMPROVED_PIPE;
            case ADVANCED:
                return RefinedPipesTileEntities.ADVANCED_PIPE;
            default:
                throw new RuntimeException("?");
        }
    }

    public ResourceLocation getId() {
        switch (this) {
            case SIMPLE:
                return new ResourceLocation(RefinedPipes.ID, "simple_pipe");
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "basic_pipe");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "improved_pipe");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "advanced_pipe");
            default:
                throw new RuntimeException("?");
        }
    }
}
