package com.raoulvdberge.refinedpipes.network.pipe.transport;

import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.TransportCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Deque;

public class ItemTransport {
    private final ItemStack value;
    private final BlockPos source;
    private final BlockPos destination;
    private final Deque<BlockPos> path;
    private final Direction initialDirection;
    private final TransportCallback finishedCallback;
    private final TransportCallback cancelCallback;

    private boolean firstPipe = true;

    private int progressInCurrentPipe;
    private Pipe currentPipe;

    public ItemTransport(ItemStack value, BlockPos source, BlockPos destination, Deque<BlockPos> path, TransportCallback finishedCallback, TransportCallback cancelCallback) {
        this.value = value;
        this.source = source;
        this.destination = destination;
        this.path = path;
        this.initialDirection = getDirection(source, path.peek());
        this.finishedCallback = finishedCallback;
        this.cancelCallback = cancelCallback;
    }

    public Direction getDirection() {
        BlockPos nextPipe = path.peek();

        if (nextPipe == null) {
            return getDirection(currentPipe.getPos(), destination);
        }

        return getDirection(currentPipe.getPos(), nextPipe);
    }

    private boolean onDone(Network network, World world) {
        finishedCallback.call(network, world, cancelCallback);
        return true;
    }

    private boolean onPipeGone() {
        return true;
    }


    public boolean update(Network network) {
        // Initial tick
        if (currentPipe == null) {
            setCurrentPipe(network, path.poll());
            currentPipe.addTransport(this);
        }

        progressInCurrentPipe += 1;

        if (progressInCurrentPipe >= getMaxTicksInPipe()) {
            currentPipe.removeTransport(this);
            firstPipe = false;

            BlockPos nextPipePos = path.poll();
            if (nextPipePos == null) {
                return onDone(network, currentPipe.getWorld());
            }

            if (!setCurrentPipe(network, nextPipePos)) {
                return onPipeGone();
            }

            progressInCurrentPipe = 0;
            currentPipe.addTransport(this);
        }

        return false;
    }

    private boolean setCurrentPipe(Network network, BlockPos pos) {
        this.currentPipe = network.getGraph().getPipes().stream().filter(p -> p.getPos().equals(pos)).findFirst().orElse(null);

        return currentPipe != null;
    }

    private boolean isLastPipe() {
        return path.isEmpty();
    }

    private static Direction getDirection(BlockPos a, BlockPos b) {
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

    private int getMaxTicksInPipe() {
        double mt = currentPipe.getMaxTicksInPipe();

        if (firstPipe) {
            mt *= 1.5D;
        }

        if (isLastPipe()) {
            mt *= 0.50D;
        }

        return (int) mt;
    }

    public ItemTransportProps createProps() {
        return new ItemTransportProps(
            value,
            currentPipe.getMaxTicksInPipe(),
            progressInCurrentPipe,
            getDirection(),
            initialDirection,
            isLastPipe(),
            firstPipe
        );
    }
}
