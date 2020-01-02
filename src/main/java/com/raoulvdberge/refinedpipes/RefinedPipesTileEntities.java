package com.raoulvdberge.refinedpipes;

import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class RefinedPipesTileEntities {
    @ObjectHolder(RefinedPipes.ID + ":pipe")
    public static final TileEntityType<PipeTileEntity> PIPE = null;
}
