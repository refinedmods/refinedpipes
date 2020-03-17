package com.raoulvdberge.refinedpipes.network.pipe.transport;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;

public class ItemTransportProps {
    private final ItemStack stack;
    private final int maxTicksInPipe;
    private int progress;
    private final Direction direction;
    private final Direction initialDirection;
    private final boolean lastPipe;
    private final boolean firstPipe;

    public ItemTransportProps(ItemStack stack, int maxTicksInPipe, int progress, Direction direction, Direction initialDirection, boolean lastPipe, boolean firstPipe) {
        this.stack = stack;
        this.maxTicksInPipe = maxTicksInPipe;
        this.progress = progress;
        this.direction = direction;
        this.initialDirection = initialDirection;
        this.lastPipe = lastPipe;
        this.firstPipe = firstPipe;
    }

    public void writeToBuffer(PacketBuffer buf) {
        buf.writeItemStack(stack);
        buf.writeInt(maxTicksInPipe);
        buf.writeInt(progress);
        buf.writeInt(direction.ordinal());
        buf.writeInt(initialDirection.ordinal());
        buf.writeBoolean(lastPipe);
        buf.writeBoolean(firstPipe);
    }

    public static ItemTransportProps create(PacketBuffer buf) {
        return new ItemTransportProps(
            buf.readItemStack(),
            buf.readInt(),
            buf.readInt(),
            Direction.values()[buf.readInt()],
            Direction.values()[buf.readInt()],
            buf.readBoolean(),
            buf.readBoolean()
        );
    }

    public void tick() {
        progress++;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getMaxTicksInPipe() {
        return maxTicksInPipe;
    }

    public int getProgress() {
        return progress;
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getInitialDirection() {
        return initialDirection;
    }

    public boolean isLastPipe() {
        return lastPipe;
    }

    public boolean isFirstPipe() {
        return firstPipe;
    }
}
