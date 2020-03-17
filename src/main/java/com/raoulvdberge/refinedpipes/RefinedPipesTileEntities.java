package com.raoulvdberge.refinedpipes;

import com.raoulvdberge.refinedpipes.tile.PipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class RefinedPipesTileEntities {
    @ObjectHolder(RefinedPipes.ID + ":simple_pipe")
    public static final TileEntityType<PipeTileEntity> SIMPLE_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":basic_pipe")
    public static final TileEntityType<PipeTileEntity> BASIC_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":improved_pipe")
    public static final TileEntityType<PipeTileEntity> IMPROVED_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":advanced_pipe")
    public static final TileEntityType<PipeTileEntity> ADVANCED_PIPE = null;
}
