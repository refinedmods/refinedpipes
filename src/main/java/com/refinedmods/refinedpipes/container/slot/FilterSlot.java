package com.refinedmods.refinedpipes.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class FilterSlot extends SlotItemHandler {
    public FilterSlot(IItemHandler handler, int inventoryIndex, int x, int y) {
        super(handler, inventoryIndex, x, y);
    }

    @Override
    public void set(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }

        super.set(stack);
    }
}
