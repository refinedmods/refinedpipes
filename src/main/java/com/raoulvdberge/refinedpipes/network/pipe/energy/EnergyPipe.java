package com.raoulvdberge.refinedpipes.network.pipe.energy;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.network.energy.EnergyNetwork;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyPipe extends Pipe {
    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "energy");

    private final EnergyPipeType type;

    public EnergyPipe(World world, BlockPos pos, EnergyPipeType type) {
        super(world, pos);
        this.type = type;
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
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
        return EnergyNetwork.TYPE;
    }
}
