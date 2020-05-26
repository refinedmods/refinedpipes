package com.refinedmods.refinedpipes.network.pipe.fluid;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.RefinedPipesTileEntities;
import com.refinedmods.refinedpipes.tile.FluidPipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public enum FluidPipeType {
    BASIC(1),
    IMPROVED(2),
    ADVANCED(3),
    ELITE(4),
    ULTIMATE(5);

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
            case ELITE:
                return RefinedPipesTileEntities.ELITE_FLUID_PIPE;
            case ULTIMATE:
                return RefinedPipesTileEntities.ULTIMATE_FLUID_PIPE;
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
            case ELITE:
                return RefinedPipes.SERVER_CONFIG.getEliteFluidPipe().getCapacity();
            case ULTIMATE:
                return RefinedPipes.SERVER_CONFIG.getUltimateFluidPipe().getCapacity();
            default:
                throw new RuntimeException("?");
        }
    }

    public int getTransferRate() {
        switch (this) {
            case BASIC:
                return RefinedPipes.SERVER_CONFIG.getBasicFluidPipe().getTransferRate();
            case IMPROVED:
                return RefinedPipes.SERVER_CONFIG.getImprovedFluidPipe().getTransferRate();
            case ADVANCED:
                return RefinedPipes.SERVER_CONFIG.getAdvancedFluidPipe().getTransferRate();
            case ELITE:
                return RefinedPipes.SERVER_CONFIG.getEliteFluidPipe().getTransferRate();
            case ULTIMATE:
                return RefinedPipes.SERVER_CONFIG.getUltimateFluidPipe().getTransferRate();
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
            case ELITE:
                return new ResourceLocation(RefinedPipes.ID, "elite_fluid_pipe");
            case ULTIMATE:
                return new ResourceLocation(RefinedPipes.ID, "ultimate_fluid_pipe");
            default:
                throw new RuntimeException("?");
        }
    }

    public ResourceLocation getNetworkType() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "basic_fluid_network");
            case IMPROVED:
                return new ResourceLocation(RefinedPipes.ID, "improved_fluid_network");
            case ADVANCED:
                return new ResourceLocation(RefinedPipes.ID, "advanced_fluid_network");
            case ELITE:
                return new ResourceLocation(RefinedPipes.ID, "elite_fluid_network");
            case ULTIMATE:
                return new ResourceLocation(RefinedPipes.ID, "ultimate_fluid_network");
            default:
                throw new RuntimeException("?");
        }
    }
}
