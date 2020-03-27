package com.raoulvdberge.refinedpipes;

import com.raoulvdberge.refinedpipes.tile.FluidPipeTileEntity;
import com.raoulvdberge.refinedpipes.tile.ItemPipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class RefinedPipesTileEntities {
    @ObjectHolder(RefinedPipes.ID + ":basic_item_pipe")
    public static final TileEntityType<ItemPipeTileEntity> BASIC_ITEM_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":improved_item_pipe")
    public static final TileEntityType<ItemPipeTileEntity> IMPROVED_ITEM_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":advanced_item_pipe")
    public static final TileEntityType<ItemPipeTileEntity> ADVANCED_ITEM_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":basic_fluid_pipe")
    public static final TileEntityType<FluidPipeTileEntity> BASIC_FLUID_PIPE = null;
}
