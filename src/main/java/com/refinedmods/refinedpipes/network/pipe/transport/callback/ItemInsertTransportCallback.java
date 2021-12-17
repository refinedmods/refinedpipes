package com.refinedmods.refinedpipes.network.pipe.transport.callback;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ItemInsertTransportCallback implements TransportCallback {
    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "item_insert");
    private static final Logger LOGGER = LogManager.getLogger(ItemInsertTransportCallback.class);
    private final BlockPos itemHandlerPosition;
    private final Direction incomingDirection;
    private final ItemStack toInsert;

    public ItemInsertTransportCallback(BlockPos itemHandlerPosition, Direction incomingDirection, ItemStack toInsert) {
        this.itemHandlerPosition = itemHandlerPosition;
        this.incomingDirection = incomingDirection;
        this.toInsert = toInsert;
    }

    @Nullable
    public static ItemInsertTransportCallback of(CompoundTag tag) {
        BlockPos itemHandlerPosition = BlockPos.of(tag.getLong("ihpos"));
        ItemStack toInsert = ItemStack.of(tag.getCompound("s"));
        Direction incomingDirection = DirectionUtil.safeGet((byte) tag.getInt("incdir"));

        if (toInsert.isEmpty()) {
            LOGGER.warn("Item no longer exists");
            return null;
        }

        return new ItemInsertTransportCallback(itemHandlerPosition, incomingDirection, toInsert);
    }

    @Override
    public void call(Network network, Level level, BlockPos currentPos, TransportCallback cancelCallback) {
        BlockEntity blockEntity = level.getBlockEntity(itemHandlerPosition);
        if (blockEntity == null) {
            LOGGER.warn("Destination item handler is gone at " + itemHandlerPosition);
            cancelCallback.call(network, level, currentPos, cancelCallback);
            return;
        }

        IItemHandler itemHandler = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, incomingDirection.getOpposite()).orElse(null);
        if (itemHandler == null) {
            LOGGER.warn("Destination item handler is no longer exposing a capability at " + itemHandlerPosition);
            cancelCallback.call(network, level, currentPos, cancelCallback);
            return;
        }

        if (ItemHandlerHelper.insertItem(itemHandler, toInsert, true).isEmpty()) {
            ItemHandlerHelper.insertItem(itemHandler, toInsert, false);
        } else {
            cancelCallback.call(network, level, currentPos, cancelCallback);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putLong("ihpos", itemHandlerPosition.asLong());
        tag.put("s", toInsert.save(new CompoundTag()));
        tag.putInt("incdir", incomingDirection.ordinal());

        return tag;
    }
}
