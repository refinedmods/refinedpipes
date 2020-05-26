package com.refinedmods.refinedpipes.container.slot;

import com.refinedmods.refinedpipes.inventory.fluid.FluidInventory;
import com.refinedmods.refinedpipes.util.FluidUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class FluidFilterSlot extends SlotItemHandler {
    private FluidInventory fluidInventory;

    public FluidFilterSlot(FluidInventory inventory, int inventoryIndex, int x, int y) {
        super(new ItemStackHandler(inventory.getSlots()), inventoryIndex, x, y);

        this.fluidInventory = inventory;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
    }

    public void onContainerClicked(@Nonnull ItemStack stack) {
        fluidInventory.setFluid(getSlotIndex(), FluidUtil.getFromStack(stack, true).getValue());
    }

    public FluidInventory getFluidInventory() {
        return fluidInventory;
    }
}
