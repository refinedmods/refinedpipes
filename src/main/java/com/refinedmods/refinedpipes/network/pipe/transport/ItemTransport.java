package com.refinedmods.refinedpipes.network.pipe.transport;

import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipe;
import com.refinedmods.refinedpipes.network.pipe.transport.callback.TransportCallback;
import com.refinedmods.refinedpipes.network.pipe.transport.callback.TransportCallbackFactory;
import com.refinedmods.refinedpipes.network.pipe.transport.callback.TransportCallbackFactoryRegistry;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

public class ItemTransport {
    private static final Logger LOGGER = LogManager.getLogger(ItemTransport.class);

    private final ItemStack value;
    private final BlockPos source;
    private final BlockPos destination;
    private final Deque<BlockPos> path;
    private final Direction initialDirection;
    private final TransportCallback finishedCallback;
    private final TransportCallback cancelCallback;
    private final TransportCallback pipeGoneCallback;
    private boolean firstPipe = true;
    private int progressInCurrentPipe;

    public ItemTransport(ItemStack value, BlockPos source, BlockPos destination, Deque<BlockPos> path, TransportCallback finishedCallback, TransportCallback cancelCallback, TransportCallback pipeGoneCallback) {
        this.value = value;
        this.source = source;
        this.destination = destination;
        this.path = path;
        this.initialDirection = getDirection(source, path.peek());
        this.path.poll(); // Pop first pipe.
        this.finishedCallback = finishedCallback;
        this.cancelCallback = cancelCallback;
        this.pipeGoneCallback = pipeGoneCallback;
    }

    public ItemTransport(ItemStack value, BlockPos source, BlockPos destination, Deque<BlockPos> path, Direction initialDirection, TransportCallback finishedCallback, TransportCallback cancelCallback, TransportCallback pipeGoneCallback, boolean firstPipe, int progressInCurrentPipe) {
        this.value = value;
        this.source = source;
        this.destination = destination;
        this.path = path;
        this.initialDirection = initialDirection;
        this.finishedCallback = finishedCallback;
        this.cancelCallback = cancelCallback;
        this.pipeGoneCallback = pipeGoneCallback;
        this.firstPipe = firstPipe;
        this.progressInCurrentPipe = progressInCurrentPipe;
    }

    private static Direction getDirection(BlockPos a, BlockPos b) {
        if (a.relative(Direction.NORTH).equals(b)) {
            return Direction.NORTH;
        }

        if (a.relative(Direction.EAST).equals(b)) {
            return Direction.EAST;
        }

        if (a.relative(Direction.SOUTH).equals(b)) {
            return Direction.SOUTH;
        }

        if (a.relative(Direction.WEST).equals(b)) {
            return Direction.WEST;
        }

        if (a.relative(Direction.UP).equals(b)) {
            return Direction.UP;
        }

        if (a.relative(Direction.DOWN).equals(b)) {
            return Direction.DOWN;
        }

        return Direction.NORTH;
    }

    @Nullable
    public static ItemTransport of(CompoundTag tag) {
        ItemStack value = ItemStack.of(tag.getCompound("v"));
        if (value.isEmpty()) {
            LOGGER.warn("Item no longer exists");
            return null;
        }

        BlockPos source = BlockPos.of(tag.getLong("src"));
        BlockPos destination = BlockPos.of(tag.getLong("dst"));

        ListTag pathTag = tag.getList("pth", Tag.TAG_LONG);
        Deque<BlockPos> path = new ArrayDeque<>();
        for (Tag pathItem : pathTag) {
            path.add(BlockPos.of(((LongTag) pathItem).getAsLong()));
        }

        Direction initialDirection = DirectionUtil.safeGet((byte) tag.getInt("initd"));

        ResourceLocation finishedCallbackId = new ResourceLocation(tag.getString("fcid"));
        TransportCallbackFactory finishedCallbackFactory = TransportCallbackFactoryRegistry.INSTANCE.getFactory(finishedCallbackId);
        if (finishedCallbackFactory == null) {
            LOGGER.warn("Finished callback factory " + finishedCallbackId + " no longer exists");
            return null;
        }
        TransportCallback finishedCallback = finishedCallbackFactory.create(tag.getCompound("fc"));
        if (finishedCallback == null) {
            LOGGER.warn("Finished callback factory " + finishedCallbackId + " returned null!");
            return null;
        }

        ResourceLocation cancelCallbackId = new ResourceLocation(tag.getString("ccid"));
        TransportCallbackFactory cancelCallbackFactory = TransportCallbackFactoryRegistry.INSTANCE.getFactory(cancelCallbackId);
        if (cancelCallbackFactory == null) {
            LOGGER.warn("Cancel callback factory " + cancelCallbackId + " no longer exists");
            return null;
        }
        TransportCallback cancelCallback = cancelCallbackFactory.create(tag.getCompound("cc"));
        if (cancelCallback == null) {
            LOGGER.warn("Cancel callback factory " + cancelCallbackId + " returned null!");
            return null;
        }

        ResourceLocation pipeGoneCallbackId = new ResourceLocation(tag.getString("pgcid"));
        TransportCallbackFactory pipeGoneCallbackFactory = TransportCallbackFactoryRegistry.INSTANCE.getFactory(pipeGoneCallbackId);
        if (pipeGoneCallbackFactory == null) {
            LOGGER.warn("Pipe gone callback factory " + pipeGoneCallbackId + " no longer exists");
            return null;
        }
        TransportCallback pipeGoneCallback = pipeGoneCallbackFactory.create(tag.getCompound("pgc"));
        if (pipeGoneCallback == null) {
            LOGGER.warn("Pipe gone callback factory " + pipeGoneCallbackId + " returned null!");
            return null;
        }

        boolean firstPipe = tag.getBoolean("fp");
        int progressInCurrentPipe = tag.getInt("p");

        return new ItemTransport(
            value,
            source,
            destination,
            path,
            initialDirection,
            finishedCallback,
            cancelCallback,
            pipeGoneCallback,
            firstPipe,
            progressInCurrentPipe
        );
    }

    public ItemStack getValue() {
        return value;
    }

    public Direction getDirection(ItemPipe currentPipe) {
        BlockPos nextPipe = path.peek();

        if (nextPipe == null) {
            return getDirection(currentPipe.getPos(), destination);
        }

        return getDirection(currentPipe.getPos(), nextPipe);
    }

    private boolean onDone(Network network, Level level, ItemPipe currentPipe) {
        finishedCallback.call(network, level, currentPipe.getPos(), cancelCallback);
        return true;
    }

    private boolean onPipeGone(Network network, Level level, BlockPos posWherePipeIsGone) {
        LOGGER.warn("Pipe on path is gone");
        pipeGoneCallback.call(network, level, posWherePipeIsGone, cancelCallback);
        return true;
    }

    public boolean update(Network network, ItemPipe currentPipe) {
        progressInCurrentPipe += 1;

        double progress = (double) progressInCurrentPipe / (double) getMaxTicksInPipe(currentPipe);

        BlockPos nextPos = currentPipe.getPos().relative(getDirection(currentPipe));
        if (progress > 0.25 && currentPipe.getLevel().isEmptyBlock(nextPos)) {
            currentPipe.removeTransport(this);
            return onPipeGone(network, currentPipe.getLevel(), nextPos);
        }

        if (progressInCurrentPipe >= getMaxTicksInPipe(currentPipe)) {
            currentPipe.removeTransport(this);
            firstPipe = false;

            BlockPos nextPipePos = path.poll();
            if (nextPipePos == null) {
                return onDone(network, currentPipe.getLevel(), currentPipe);
            }

            Pipe nextPipe = network.getPipe(nextPipePos);
            if (nextPipe == null) {
                return onPipeGone(network, currentPipe.getLevel(), nextPipePos);
            }

            progressInCurrentPipe = 0;
            ((ItemPipe) nextPipe).addTransport(this);
        }

        return false;
    }

    private boolean isLastPipe() {
        return path.isEmpty();
    }

    private int getMaxTicksInPipe(ItemPipe currentPipe) {
        double mt = currentPipe.getMaxTicksInPipe();

        if (firstPipe) {
            mt *= 1.25D;
        }

        if (isLastPipe()) {
            mt *= 0.25D;
        }

        return (int) mt;
    }

    public ItemTransportProps createProps(ItemPipe currentPipe) {
        return new ItemTransportProps(
            value,
            currentPipe.getMaxTicksInPipe(),
            progressInCurrentPipe,
            getDirection(currentPipe),
            initialDirection,
            isLastPipe(),
            firstPipe
        );
    }

    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.put("v", value.save(new CompoundTag()));
        tag.putLong("src", source.asLong());
        tag.putLong("dst", destination.asLong());

        ListTag path = new ListTag();
        for (BlockPos pathItem : this.path) {
            path.add(LongTag.valueOf(pathItem.asLong()));
        }
        tag.put("pth", path);

        tag.putInt("initiald", initialDirection.ordinal());

        tag.put("fc", finishedCallback.writeToNbt(new CompoundTag()));
        tag.putString("fcid", finishedCallback.getId().toString());
        tag.put("cc", cancelCallback.writeToNbt(new CompoundTag()));
        tag.putString("ccid", cancelCallback.getId().toString());
        tag.put("pgc", pipeGoneCallback.writeToNbt(new CompoundTag()));
        tag.putString("pgcid", pipeGoneCallback.getId().toString());

        tag.putBoolean("fp", firstPipe);
        tag.putInt("p", progressInCurrentPipe);

        return tag;
    }
}
