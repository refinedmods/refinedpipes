package com.refinedmods.refinedpipes.network.pipe.transport;

import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.network.pipe.Pipe;
import com.refinedmods.refinedpipes.network.pipe.item.ItemPipe;
import com.refinedmods.refinedpipes.network.pipe.transport.callback.TransportCallback;
import com.refinedmods.refinedpipes.network.pipe.transport.callback.TransportCallbackFactory;
import com.refinedmods.refinedpipes.network.pipe.transport.callback.TransportCallbackFactoryRegistry;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
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

    private boolean onDone(Network network, World world, ItemPipe currentPipe) {
        finishedCallback.call(network, world, currentPipe.getPos(), cancelCallback);
        return true;
    }

    private boolean onPipeGone(Network network, World world, BlockPos posWherePipeIsGone) {
        LOGGER.warn("Pipe on path is gone");
        pipeGoneCallback.call(network, world, posWherePipeIsGone, cancelCallback);
        return true;
    }

    public boolean update(Network network, ItemPipe currentPipe) {
        progressInCurrentPipe += 1;

        double progress = (double) progressInCurrentPipe / (double) getMaxTicksInPipe(currentPipe);

        BlockPos nextPos = currentPipe.getPos().offset(getDirection(currentPipe));
        if (progress > 0.25 && currentPipe.getWorld().isAirBlock(nextPos)) {
            currentPipe.removeTransport(this);
            return onPipeGone(network, currentPipe.getWorld(), nextPos);
        }

        if (progressInCurrentPipe >= getMaxTicksInPipe(currentPipe)) {
            currentPipe.removeTransport(this);
            firstPipe = false;

            BlockPos nextPipePos = path.poll();
            if (nextPipePos == null) {
                return onDone(network, currentPipe.getWorld(), currentPipe);
            }

            Pipe nextPipe = network.getPipe(nextPipePos);
            if (nextPipe == null) {
                return onPipeGone(network, currentPipe.getWorld(), nextPipePos);
            }

            progressInCurrentPipe = 0;
            ((ItemPipe) nextPipe).addTransport(this);
        }

        return false;
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

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.put("v", value.write(new CompoundNBT()));
        tag.putLong("src", source.toLong());
        tag.putLong("dst", destination.toLong());

        ListNBT path = new ListNBT();
        for (BlockPos pathItem : this.path) {
            path.add(LongNBT.valueOf(pathItem.toLong()));
        }
        tag.put("pth", path);

        tag.putInt("initiald", initialDirection.ordinal());

        tag.put("fc", finishedCallback.writeToNbt(new CompoundNBT()));
        tag.putString("fcid", finishedCallback.getId().toString());
        tag.put("cc", cancelCallback.writeToNbt(new CompoundNBT()));
        tag.putString("ccid", cancelCallback.getId().toString());
        tag.put("pgc", pipeGoneCallback.writeToNbt(new CompoundNBT()));
        tag.putString("pgcid", pipeGoneCallback.getId().toString());

        tag.putBoolean("fp", firstPipe);
        tag.putInt("p", progressInCurrentPipe);

        return tag;
    }

    @Nullable
    public static ItemTransport of(CompoundNBT tag) {
        ItemStack value = ItemStack.read(tag.getCompound("v"));
        if (value.isEmpty()) {
            LOGGER.warn("Item no longer exists");
            return null;
        }

        BlockPos source = BlockPos.fromLong(tag.getLong("src"));
        BlockPos destination = BlockPos.fromLong(tag.getLong("dst"));

        ListNBT pathTag = tag.getList("pth", Constants.NBT.TAG_LONG);
        Deque<BlockPos> path = new ArrayDeque<>();
        for (INBT pathItem : pathTag) {
            path.add(BlockPos.fromLong(((LongNBT) pathItem).getLong()));
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
}
