package com.refinedmods.refinedpipes.network.pipe.transport.callback;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ItemBounceBackTransportCallback implements TransportCallback {
    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "item_bounce_back");
    private static final Logger LOGGER = LogManager.getLogger(ItemBounceBackTransportCallback.class);
    private final BlockPos originalItemHandlerPosition;
    private final BlockPos bounceBackItemHandlerPosition;
    private final ItemStack toInsert;

    public ItemBounceBackTransportCallback(BlockPos originalItemHandlerPosition, BlockPos bounceBackItemHandlerPosition, ItemStack toInsert) {
        this.originalItemHandlerPosition = originalItemHandlerPosition;
        this.bounceBackItemHandlerPosition = bounceBackItemHandlerPosition;
        this.toInsert = toInsert;
    }

    @Nullable
    public static ItemBounceBackTransportCallback of(CompoundTag tag) {
        BlockPos originalItemHandlerPosition = BlockPos.of(tag.getLong("oihpos"));
        BlockPos bounceBackItemHandlerPosition = BlockPos.of(tag.getLong("bbihpos"));
        ItemStack toInsert = ItemStack.of(tag.getCompound("s"));

        if (toInsert.isEmpty()) {
            LOGGER.warn("Item no longer exists");
            return null;
        }

        return new ItemBounceBackTransportCallback(originalItemHandlerPosition, bounceBackItemHandlerPosition, toInsert);
    }

    @Override
    public void call(Network network, Level level, BlockPos currentPos, TransportCallback cancelCallback) {
        // TODO: Actually bounce back...
        Containers.dropItemStack(level, originalItemHandlerPosition.getX(), originalItemHandlerPosition.getY(), originalItemHandlerPosition.getZ(), toInsert);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putLong("oihpos", originalItemHandlerPosition.asLong());
        tag.putLong("bbihpos", bounceBackItemHandlerPosition.asLong());
        tag.put("s", toInsert.save(new CompoundTag()));

        return tag;
    }
}
