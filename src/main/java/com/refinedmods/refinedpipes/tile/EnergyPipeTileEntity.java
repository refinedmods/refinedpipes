package com.refinedmods.refinedpipes.tile;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.energy.ClientEnergyPipeEnergyStorage;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipe;
import com.refinedmods.refinedpipes.network.pipe.energy.EnergyPipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyPipeTileEntity extends PipeTileEntity {
    private final EnergyPipeType type;
    private final LazyOptional<ClientEnergyPipeEnergyStorage> clientEnergyStorage;

    public EnergyPipeTileEntity(BlockPos pos, BlockState state, EnergyPipeType type) {
        super(type.getTileType(), pos, state);

        this.type = type;
        this.clientEnergyStorage = LazyOptional.of(() -> new ClientEnergyPipeEnergyStorage(type));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            if (!level.isClientSide) {
                NetworkManager mgr = NetworkManager.get(level);

                Pipe pipe = mgr.getPipe(worldPosition);
                if (pipe instanceof EnergyPipe) {
                    return ((EnergyPipe) pipe).getEnergyStorage().cast();
                }
            }

            return clientEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected Pipe createPipe(Level world, BlockPos pos) {
        return new EnergyPipe(world, pos, type);
    }
}
