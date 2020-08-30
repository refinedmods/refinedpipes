package com.refinedmods.refinedpipes.network.pipe.transport.callback;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.Network;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ItemInsertTransportCallback implements TransportCallback {
    private static final Logger LOGGER = LogManager.getLogger(ItemInsertTransportCallback.class);

    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "item_insert");

    private final BlockPos itemHandlerPosition;
    private final Direction incomingDirection;
    private final ItemStack toInsert;

    public ItemInsertTransportCallback(BlockPos itemHandlerPosition, Direction incomingDirection, ItemStack toInsert) {
        this.itemHandlerPosition = itemHandlerPosition;
        this.incomingDirection = incomingDirection;
        this.toInsert = toInsert;
    }

    @Override
    public void call(Network network, World world, BlockPos currentPos, TransportCallback cancelCallback) {
        TileEntity tile = world.getTileEntity(itemHandlerPosition);
        if (tile == null) {
            LOGGER.warn("Destination item handler is gone at " + itemHandlerPosition);
            cancelCallback.call(network, world, currentPos, cancelCallback);
            return;
        }

        IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, incomingDirection.getOpposite()).orElse(null);
        if (itemHandler == null) {
            LOGGER.warn("Destination item handler is no longer exposing a capability at " + itemHandlerPosition);
            cancelCallback.call(network, world, currentPos, cancelCallback);
            return;
        }

        if (ItemHandlerHelper.insertItem(itemHandler, toInsert, true).isEmpty()) {
            ItemHandlerHelper.insertItem(itemHandler, toInsert, false);
        } else {
            cancelCallback.call(network, world, currentPos, cancelCallback);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nullable
    public static ItemInsertTransportCallback of(CompoundNBT tag) {
        BlockPos itemHandlerPosition = BlockPos.fromLong(tag.getLong("ihpos"));
        ItemStack toInsert = ItemStack.read(tag.getCompound("s"));
        Direction incomingDirection = DirectionUtil.safeGet((byte) tag.getInt("incdir"));

        if (toInsert.isEmpty()) {
            LOGGER.warn("Item no longer exists");
            return null;
        }

        return new ItemInsertTransportCallback(itemHandlerPosition, incomingDirection, toInsert);
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putLong("ihpos", itemHandlerPosition.toLong());
        tag.put("s", toInsert.write(new CompoundNBT()));
        tag.putInt("incdir", incomingDirection.ordinal());

        return tag;
    }
}
