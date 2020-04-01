package com.raoulvdberge.refinedpipes.tile;

import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipe;
import com.raoulvdberge.refinedpipes.network.pipe.energy.EnergyPipeType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyPipeTileEntity extends PipeTileEntity {
    private final EnergyPipeType type;

    public EnergyPipeTileEntity(EnergyPipeType type) {
        super(type.getTileType());

        this.type = type;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!world.isRemote && cap == CapabilityEnergy.ENERGY) {
            NetworkManager mgr = NetworkManager.get(world);

            Pipe pipe = mgr.getPipe(pos);
            if (pipe instanceof EnergyPipe) {
                return ((EnergyPipe) pipe).getEnergyStorage().cast();
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected Pipe createPipe(World world, BlockPos pos) {
        return new EnergyPipe(world, pos, type);
    }
}
