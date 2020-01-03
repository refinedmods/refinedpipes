package com.raoulvdberge.refinedpipes.network.pipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import java.util.Deque;

public class Transport {
    private final ItemStack value;
    private final Pipe source;
    private final Pipe destination;
    private final Deque<Pipe> pipesToGo;

    private int progressInCurrentPipe;
    private Pipe currentPipe;

    public Transport(ItemStack value, Pipe source, Pipe destination, Deque<Pipe> pipesToGo) {
        this.value = value;
        this.source = source;
        this.destination = destination;
        this.pipesToGo = pipesToGo;
    }

    public Direction getDirection() {
        // TODO
        return Direction.NORTH;
    }

    public ItemStack getValue() {
        return value;
    }

    public int getProgressInCurrentPipe() {
        return progressInCurrentPipe;
    }

    public boolean update() {
        if (currentPipe == null) {
            currentPipe = pipesToGo.poll();

            if (currentPipe != null) {
                currentPipe.setCurrentTransport(this);
                progressInCurrentPipe = 0;
            } else {
                return true;
            }
        }

        progressInCurrentPipe += 1;

        if (progressInCurrentPipe >= currentPipe.getMaxTicksInPipe()) {
            currentPipe.setCurrentTransport(null);
            currentPipe = null;
        }

        return false;
    }
}
