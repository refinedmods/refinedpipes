package com.raoulvdberge.refinedpipes.network.pipe.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyPipeEnergyStorage implements IEnergyStorage {
    private final IEnergyStorage networkEnergyStorage;

    public EnergyPipeEnergyStorage(IEnergyStorage networkEnergyStorage) {
        this.networkEnergyStorage = networkEnergyStorage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return networkEnergyStorage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return networkEnergyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return networkEnergyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
