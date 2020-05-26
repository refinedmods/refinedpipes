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

public class ItemPipeGoneTransportCallback implements TransportCallback {
    private static final Logger LOGGER = LogManager.getLogger(ItemPipeGoneTransportCallback.class);

    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "item_pipe_gone");

    private final ItemStack stack;

    public ItemPipeGoneTransportCallback(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void call(Network network, World world, BlockPos currentPos, TransportCallback cancelCallback) {
        InventoryHelper.spawnItemStack(world, currentPos.getX(), currentPos.getY(), currentPos.getZ(), stack);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nullable
    public static ItemPipeGoneTransportCallback of(CompoundNBT tag) {
        ItemStack stack = ItemStack.read(tag.getCompound("s"));

        if (stack.isEmpty()) {
            LOGGER.warn("Item no longer exists");
            return null;
        }

        return new ItemPipeGoneTransportCallback(stack);
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.put("s", stack.write(new CompoundNBT()));

        return tag;
    }
}
