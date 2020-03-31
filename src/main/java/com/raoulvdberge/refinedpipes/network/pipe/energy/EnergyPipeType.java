package com.raoulvdberge.refinedpipes.network.pipe.energy;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesTileEntities;
import com.raoulvdberge.refinedpipes.tile.EnergyPipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public enum EnergyPipeType {
    BASIC(1);

    private final int tier;

    EnergyPipeType(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public ResourceLocation getId() {
        switch (this) {
            case BASIC:
                return new ResourceLocation(RefinedPipes.ID, "basic_energy_pipe");
            default:
                throw new RuntimeException("?");
        }
    }

    public TileEntityType<EnergyPipeTileEntity> getTileType() {
        switch (this) {
            case BASIC:
                return RefinedPipesTileEntities.BASIC_ENERGY_PIPE;
            default:
                throw new RuntimeException("?");
        }
    }
}
