package com.raoulvdberge.refinedpipes.network.pipe.fluid;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.tile.FluidPipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public enum FluidPipeType {
    BASIC(1),
    IMPROVED(2),
    ADVANCED(3);

    private final int tier;

    FluidPipeType(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public TileEntityType<FluidPipeTileEntity> getTileType() {
        switch (this) {
            case BASIC:
                return RefinedPipesTileEntities.BASIC_FLUID_PIPE;
            case IMPROVED:
                return RefinedPipesTileEntities.IMPROVED_FLUID_PIPE;
            case ADVANCED:
                return RefinedPipesTileEntities.ADVANCED_FLUID_PIPE;
            default:
                throw new RuntimeException("?");
        }
    }

    public int getCapacity() {
        switch (this) {
            case BASIC:
                return RefinedPipes.SERVER_CONFIG.getBasicFluidPipe().getCapacity();
            case IMPROVED:
                return RefinedPipes.SERVER_CONFIG.getImprovedFluidPipe().getCapacity();
            case ADVANCED:
                return RefinedPipes.SERVER_CONFIG.getAdvancedFluidPipe().getCapacity();
            default:
                throw new RuntimeException("?");
        }
    }

    public ResourceLocation getId() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "basic_fluid_pipe");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "improved_fluid_pipe");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "advanced_fluid_pipe");
            default:
                throw new RuntimeException("?");
        }
    }
}
