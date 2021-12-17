package com.refinedmods.refinedpipes.container.slot;

import com.refinedmods.refinedpipes.inventory.fluid.FluidInventory;
import com.refinedmods.refinedpipes.util.FluidUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class FluidFilterSlot extends SlotItemHandler {
    private final int inventoryIndex;
    private FluidInventory fluidInventory;

    public FluidFilterSlot(FluidInventory inventory, int inventoryIndex, int x, int y) {
        super(new ItemStackHandler(inventory.getSlots()), inventoryIndex, x, y);

        this.fluidInventory = inventory;
        this.inventoryIndex = inventoryIndex;
    }

    public int getInventoryIndex() {
        return inventoryIndex;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }

    public void onContainerClicked(@Nonnull ItemStack stack) {
        fluidInventory.setFluid(getSlotIndex(), FluidUtil.getFromStack(stack, true).getValue());
    }

    public FluidInventory getFluidInventory() {
        return fluidInventory;
    }
}
