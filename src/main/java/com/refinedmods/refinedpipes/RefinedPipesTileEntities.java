package com.refinedmods.refinedpipes;

import com.refinedmods.refinedpipes.tile.EnergyPipeTileEntity;
import com.refinedmods.refinedpipes.tile.FluidPipeTileEntity;
import com.refinedmods.refinedpipes.tile.ItemPipeTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class RefinedPipesTileEntities {
    @ObjectHolder(RefinedPipes.ID + ":basic_item_pipe")
    public static final BlockEntityType<ItemPipeTileEntity> BASIC_ITEM_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":improved_item_pipe")
    public static final BlockEntityType<ItemPipeTileEntity> IMPROVED_ITEM_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":advanced_item_pipe")
    public static final BlockEntityType<ItemPipeTileEntity> ADVANCED_ITEM_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":basic_fluid_pipe")
    public static final BlockEntityType<FluidPipeTileEntity> BASIC_FLUID_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":improved_fluid_pipe")
    public static final BlockEntityType<FluidPipeTileEntity> IMPROVED_FLUID_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":advanced_fluid_pipe")
    public static final BlockEntityType<FluidPipeTileEntity> ADVANCED_FLUID_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":elite_fluid_pipe")
    public static final BlockEntityType<FluidPipeTileEntity> ELITE_FLUID_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":ultimate_fluid_pipe")
    public static final BlockEntityType<FluidPipeTileEntity> ULTIMATE_FLUID_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":basic_energy_pipe")
    public static final BlockEntityType<EnergyPipeTileEntity> BASIC_ENERGY_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":improved_energy_pipe")
    public static final BlockEntityType<EnergyPipeTileEntity> IMPROVED_ENERGY_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":advanced_energy_pipe")
    public static final BlockEntityType<EnergyPipeTileEntity> ADVANCED_ENERGY_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":elite_energy_pipe")
    public static final BlockEntityType<EnergyPipeTileEntity> ELITE_ENERGY_PIPE = null;
    @ObjectHolder(RefinedPipes.ID + ":ultimate_energy_pipe")
    public static final BlockEntityType<EnergyPipeTileEntity> ULTIMATE_ENERGY_PIPE = null;
}
