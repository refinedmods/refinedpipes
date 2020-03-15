package com.raoulvdberge.refinedpipes.network.pipe.transport.callback;

import com.raoulvdberge.refinedpipes.network.Network;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemInsertTransportCallback implements TransportCallback {
    private final BlockPos itemHandlerPosition;
    private final ItemStack toInsert;

    public ItemInsertTransportCallback(BlockPos itemHandlerPosition, ItemStack toInsert) {
        this.itemHandlerPosition = itemHandlerPosition;
        this.toInsert = toInsert;
    }

    @Override
    public void call(Network network, World world, TransportCallback cancelCallback) {
        TileEntity tile = world.getTileEntity(itemHandlerPosition);
        if (tile == null) {
            cancelCallback.call(network, world, cancelCallback);
            return;
        }

        IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
        if (itemHandler == null) {
            cancelCallback.call(network, world, cancelCallback);
            return;
        }

        if (ItemHandlerHelper.insertItem(itemHandler, toInsert, true).isEmpty()) {
            ItemHandlerHelper.insertItem(itemHandler, toInsert, false);
        } else {
            cancelCallback.call(network, world, cancelCallback);
        }
    }
}
