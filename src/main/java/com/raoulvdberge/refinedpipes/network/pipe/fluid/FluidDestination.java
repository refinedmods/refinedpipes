package com.raoulvdberge.refinedpipes.network.pipe.fluid;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class FluidDestination {
    private final BlockPos receiver;
    private final Direction incomingDirection;
    private final Pipe connectedPipe;

    public FluidDestination(BlockPos receiver, Direction incomingDirection, Pipe connectedPipe) {
        this.receiver = receiver;
        this.incomingDirection = incomingDirection;
        this.connectedPipe = connectedPipe;
    }

    public BlockPos getReceiver() {
        return receiver;
    }

    public Direction getIncomingDirection() {
        return incomingDirection;
    }

    public Pipe getConnectedPipe() {
        return connectedPipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FluidDestination that = (FluidDestination) o;
        return Objects.equals(receiver, that.receiver) &&
            incomingDirection == that.incomingDirection &&
            Objects.equals(connectedPipe.getPos(), that.connectedPipe.getPos());
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiver, incomingDirection, connectedPipe.getPos());
    }
}
