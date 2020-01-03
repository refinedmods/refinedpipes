package com.raoulvdberge.refinedpipes.network.pipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Deque;

public class Transport {
    private final ItemStack value;
    private final Pipe source;
    private final Pipe destination; // the destination pipe
    private final BlockPos destinationPos; // the actual destination position, usually one more offset between destination pipe
    private final Deque<Pipe> pipesToGo;

    private int progressInCurrentPipe;
    private Pipe currentPipe;

    public Transport(ItemStack value, Pipe source, Pipe destination, BlockPos destinationPos, Deque<Pipe> pipesToGo) {
        this.value = value;
        this.source = source;
        this.destination = destination;
        this.destinationPos = destinationPos;
        this.pipesToGo = pipesToGo;
    }

    public Direction getDirection() {
        Pipe nextPipe = pipesToGo.peek();

        if (nextPipe == null) {
            return getDirection(currentPipe.getPos(), destinationPos);
        }

        return getDirection(currentPipe.getPos(), nextPipe.getPos());
    }

    public boolean isLastPipe() {
        return pipesToGo.isEmpty();
    }

    // TODO make this more performant
    private Direction getDirection(BlockPos a, BlockPos b) {
        if (a.offset(Direction.NORTH).equals(b)) {
            return Direction.NORTH;
        }
        if (a.offset(Direction.EAST).equals(b)) {
            return Direction.EAST;
        }
        if (a.offset(Direction.SOUTH).equals(b)) {
            return Direction.SOUTH;
        }
        if (a.offset(Direction.WEST).equals(b)) {
            return Direction.WEST;
        }
        if (a.offset(Direction.UP).equals(b)) {
            return Direction.UP;
        }
        if (a.offset(Direction.DOWN).equals(b)) {
            return Direction.DOWN;
        }
        return Direction.NORTH;
    }

    public ItemStack getValue() {
        return value;
    }

    public int getProgressInCurrentPipe() {
        return progressInCurrentPipe;
    }

    private boolean initial = true;

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

        if (progressInCurrentPipe >= getMaxTicksInPipe()) {
            currentPipe.setCurrentTransport(null);
            currentPipe = null;
            initial = false;
        }

        return false;
    }

    private int getMaxTicksInPipe() {
        double mt = currentPipe.getMaxTicksInPipe();

        if (isFirstPipe()) {
            mt *= 1.5D;
        }

        if (isLastPipe()) {
            mt *= 0.5D;
        }

        return (int) mt;
    }

    public boolean isFirstPipe() {
        return initial;
    }
}
