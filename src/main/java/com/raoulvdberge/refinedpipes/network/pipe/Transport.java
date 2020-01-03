package com.raoulvdberge.refinedpipes.network.pipe;

import net.minecraft.item.ItemStack;

import java.util.Deque;

public class Transport {
    private final ItemStack value;
    private final Pipe source;
    private final Pipe destination;
    private final Deque<Pipe> pipesToGo;

    private double progressInCurrentPipe;
    private Pipe currentPipe;

    public Transport(ItemStack value, Pipe source, Pipe destination, Deque<Pipe> pipesToGo) {
        this.value = value;
        this.source = source;
        this.destination = destination;
        this.pipesToGo = pipesToGo;
    }

    public ItemStack getValue() {
        return value;
    }

    public boolean update() {
        if (currentPipe == null) {
            currentPipe = pipesToGo.poll();

            if (currentPipe != null) {
                currentPipe.setCurrentTransport(this);
            } else {
                return true;
            }
        }

        progressInCurrentPipe += 0.1;

        if (progressInCurrentPipe >= 1) {
            this.currentPipe.setCurrentTransport(null);
            this.currentPipe = null;
            this.progressInCurrentPipe = 0;
        }

        return false;
    }
}
