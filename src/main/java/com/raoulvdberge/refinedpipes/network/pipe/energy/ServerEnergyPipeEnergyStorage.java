package com.raoulvdberge.refinedpipes.network.pipe.energy;

import com.raoulvdberge.refinedpipes.network.energy.EnergyNetwork;
import net.minecraftforge.energy.IEnergyStorage;

public class ServerEnergyPipeEnergyStorage implements IEnergyStorage, EnergyPipeEnergyStorage {
    private final EnergyNetwork network;

    public ServerEnergyPipeEnergyStorage(EnergyNetwork network) {
        this.network = network;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return network.getEnergyStorage().receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return network.getEnergyStorage().getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return network.getEnergyStorage().getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public EnergyPipeType getEnergyPipeType() {
        return network.getPipeType();
    }
}
