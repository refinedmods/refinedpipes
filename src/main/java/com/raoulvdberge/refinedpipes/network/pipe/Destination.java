package com.raoulvdberge.refinedpipes.network.pipe;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class Destination {
    private final BlockPos receiver;
    private final Direction incomingDirection;
    private final ItemPipe connectedPipe;

    public Destination(BlockPos receiver, Direction incomingDirection, ItemPipe connectedPipe) {
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

    public ItemPipe getConnectedPipe() {
        return connectedPipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return Objects.equals(receiver, that.receiver) &&
            incomingDirection == that.incomingDirection &&
            Objects.equals(connectedPipe.getPos(), that.connectedPipe.getPos());
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiver, incomingDirection, connectedPipe.getPos());
    }
}
