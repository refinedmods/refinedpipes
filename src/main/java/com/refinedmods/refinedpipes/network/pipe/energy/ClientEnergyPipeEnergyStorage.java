package com.refinedmods.refinedpipes.network.pipe.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class ClientEnergyPipeEnergyStorage implements IEnergyStorage, EnergyPipeEnergyStorage {
    private final EnergyPipeType pipeType;

    public ClientEnergyPipeEnergyStorage(EnergyPipeType pipeType) {
        this.pipeType = pipeType;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
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
        return pipeType;
    }
}
