package com.raoulvdberge.refinedpipes.network.pipe.transport.callback;

import com.raoulvdberge.refinedpipes.network.Network;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBounceBackTransportCallback implements TransportCallback {
    private final BlockPos originalItemHandlerPosition;
    private final BlockPos bounceBackItemHandlerPosition;
    private final ItemStack toInsert;

    public ItemBounceBackTransportCallback(BlockPos originalItemHandlerPosition, BlockPos bounceBackItemHandlerPosition, ItemStack toInsert) {
        this.originalItemHandlerPosition = originalItemHandlerPosition;
        this.bounceBackItemHandlerPosition = bounceBackItemHandlerPosition;
        this.toInsert = toInsert;
    }

    @Override
    public void call(Network network, World world, TransportCallback cancelCallback) {
        // TODO: Actually bounce back...
        InventoryHelper.spawnItemStack(world, originalItemHandlerPosition.getX(), originalItemHandlerPosition.getY(), originalItemHandlerPosition.getZ(), toInsert);
    }
}
