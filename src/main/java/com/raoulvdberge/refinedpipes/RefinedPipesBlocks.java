package com.raoulvdberge.refinedpipes;

import com.raoulvdberge.refinedpipes.block.EnergyPipeBlock;
import com.raoulvdberge.refinedpipes.block.FluidPipeBlock;
import com.raoulvdberge.refinedpipes.block.ItemPipeBlock;
import net.minecraftforge.registries.ObjectHolder;

public class RefinedPipesBlocks {
    @ObjectHolder(RefinedPipes.ID + ":basic_item_pipe")
    public static final ItemPipeBlock BASIC_ITEM_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":improved_item_pipe")
    public static final ItemPipeBlock IMPROVED_ITEM_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":advanced_item_pipe")
    public static final ItemPipeBlock ADVANCED_ITEM_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":basic_fluid_pipe")
    public static final FluidPipeBlock BASIC_FLUID_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":improved_fluid_pipe")
    public static final FluidPipeBlock IMPROVED_FLUID_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":advanced_fluid_pipe")
    public static final FluidPipeBlock ADVANCED_FLUID_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":basic_energy_pipe")
    public static final EnergyPipeBlock BASIC_ENERGY_PIPE = null;
}
