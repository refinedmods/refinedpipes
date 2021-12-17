package com.refinedmods.refinedpipes.network.pipe.transport;

import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ItemTransportProps {
    private final ItemStack stack;
    private final int maxTicksInPipe;
    private final Direction direction;
    private final Direction initialDirection;
    private final boolean lastPipe;
    private final boolean firstPipe;
    private int progress;

    public ItemTransportProps(ItemStack stack, int maxTicksInPipe, int progress, Direction direction, Direction initialDirection, boolean lastPipe, boolean firstPipe) {
        this.stack = stack;
        this.maxTicksInPipe = maxTicksInPipe;
        this.progress = progress;
        this.direction = direction;
        this.initialDirection = initialDirection;
        this.lastPipe = lastPipe;
        this.firstPipe = firstPipe;
    }

    public static ItemTransportProps create(FriendlyByteBuf buf) {
        return new ItemTransportProps(
            buf.readItem(),
            buf.readInt(),
            buf.readInt(),
            DirectionUtil.safeGet((byte) buf.readInt()),
            DirectionUtil.safeGet((byte) buf.readInt()),
            buf.readBoolean(),
            buf.readBoolean()
        );
    }

    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeItem(stack);
        buf.writeInt(maxTicksInPipe);
        buf.writeInt(progress);
        buf.writeInt(direction.ordinal());
        buf.writeInt(initialDirection.ordinal());
        buf.writeBoolean(lastPipe);
        buf.writeBoolean(firstPipe);
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
