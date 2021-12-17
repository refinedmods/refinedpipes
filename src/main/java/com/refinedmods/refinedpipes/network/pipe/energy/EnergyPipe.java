package com.refinedmods.refinedpipes.network.pipe.energy;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.network.energy.EnergyNetwork;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

public class EnergyPipe extends Pipe {
    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "energy");

    private final EnergyPipeType type;

    private LazyOptional<ServerEnergyPipeEnergyStorage> energyStorage = LazyOptional.empty();

    public EnergyPipe(Level world, BlockPos pos, EnergyPipeType type) {
        super(world, pos);
        this.type = type;
    }

    @Override
    public void joinNetwork(Network network) {
        super.joinNetwork(network);

        this.energyStorage = LazyOptional.of(() -> new ServerEnergyPipeEnergyStorage((EnergyNetwork) network));
    }

    @Override
    public void leaveNetwork() {
        super.leaveNetwork();

        this.energyStorage = LazyOptional.empty();
    }

    public LazyOptional<ServerEnergyPipeEnergyStorage> getEnergyStorage() {
        return energyStorage;
    }

    public EnergyPipeType getType() {
        return type;
    }

    @Override
    public CompoundTag writeToNbt(CompoundTag tag) {
        tag = super.writeToNbt(tag);

        tag.putInt("type", type.ordinal());

        return tag;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getNetworkType() {
        return type.getNetworkType();
    }
}
