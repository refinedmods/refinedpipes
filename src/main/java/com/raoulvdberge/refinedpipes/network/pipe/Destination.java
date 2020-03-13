package com.raoulvdberge.refinedpipes.network.pipe;

import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import net.minecraft.util.math.BlockPos;

public class Destination<T> {
    private final T dest;
    private final BlockPos destPos;
    private final Pipe connectedPipe;

    public Destination(T dest, BlockPos destPos, Pipe connectedPipe) {
        this.dest = dest;
        this.destPos = destPos;
        this.connectedPipe = connectedPipe;
    }

    public T getDest() {
        return dest;
    }

    public BlockPos getDestPos() {
        return destPos;
    }

    public Pipe getConnectedPipe() {
        return connectedPipe;
    }
}
