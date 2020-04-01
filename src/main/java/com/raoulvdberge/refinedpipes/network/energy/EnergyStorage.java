package com.raoulvdberge.refinedpipes.network.energy;

public class EnergyStorage extends net.minecraftforge.energy.EnergyStorage {
    public EnergyStorage(int capacity) {
        super(capacity);
    }

    public void setCapacity(int cap) {
        this.capacity = cap;
        this.maxExtract = cap;
        this.maxReceive = cap;
    }

    public void setStored(int stored) {
        this.energy = stored;
    }
}
