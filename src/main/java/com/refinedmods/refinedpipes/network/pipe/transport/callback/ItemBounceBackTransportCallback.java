package com.refinedmods.refinedpipes.network.pipe.transport.callback;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.Network;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ItemBounceBackTransportCallback implements TransportCallback {
    private static final Logger LOGGER = LogManager.getLogger(ItemBounceBackTransportCallback.class);

    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "item_bounce_back");

    private final BlockPos originalItemHandlerPosition;
    private final BlockPos bounceBackItemHandlerPosition;
    private final ItemStack toInsert;

    public ItemBounceBackTransportCallback(BlockPos originalItemHandlerPosition, BlockPos bounceBackItemHandlerPosition, ItemStack toInsert) {
        this.originalItemHandlerPosition = originalItemHandlerPosition;
        this.bounceBackItemHandlerPosition = bounceBackItemHandlerPosition;
        this.toInsert = toInsert;
    }

    @Override
    public void call(Network network, World world, BlockPos currentPos, TransportCallback cancelCallback) {
        // TODO: Actually bounce back...
        InventoryHelper.spawnItemStack(world, originalItemHandlerPosition.getX(), originalItemHandlerPosition.getY(), originalItemHandlerPosition.getZ(), toInsert);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nullable
    public static ItemBounceBackTransportCallback of(CompoundNBT tag) {
        BlockPos originalItemHandlerPosition = BlockPos.fromLong(tag.getLong("oihpos"));
        BlockPos bounceBackItemHandlerPosition = BlockPos.fromLong(tag.getLong("bbihpos"));
        ItemStack toInsert = ItemStack.read(tag.getCompound("s"));

        if (toInsert.isEmpty()) {
            LOGGER.warn("Item no longer exists");
            return null;
        }

        return new ItemBounceBackTransportCallback(originalItemHandlerPosition, bounceBackItemHandlerPosition, toInsert);
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putLong("oihpos", originalItemHandlerPosition.toLong());
        tag.putLong("bbihpos", bounceBackItemHandlerPosition.toLong());
        tag.put("s", toInsert.write(new CompoundNBT()));

        return tag;
    }
}
