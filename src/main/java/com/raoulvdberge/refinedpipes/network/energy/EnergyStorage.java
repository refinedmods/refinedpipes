package com.raoulvdberge.refinedpipes.network.energy;

public class EnergyStorage extends net.minecraftforge.energy.EnergyStorage {
    public EnergyStorage(int capacity) {
        super(capacity);
    }

    public void setCapacityAndMaxExtract(int capacity) {
        this.capacity = capacity;
        this.maxExtract = capacity;
    }

    public void setMaxReceive(int maxReceive) {
        this.maxReceive = maxReceive;
    }

    public void setStored(int stored) {
        this.energy = stored;
    }
}
