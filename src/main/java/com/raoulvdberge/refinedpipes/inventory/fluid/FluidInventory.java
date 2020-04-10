package com.raoulvdberge.refinedpipes.inventory.fluid;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class FluidInventory {
    private static final String NBT_SLOT = "Slot_%d";

    private final FluidStack[] fluids;
    private final int maxAmount;

    public FluidInventory(int size, int maxAmount) {
        this.fluids = new FluidStack[size];

        for (int i = 0; i < size; ++i) {
            fluids[i] = FluidStack.EMPTY;
        }

        this.maxAmount = maxAmount;
    }

    public FluidInventory(int size) {
        this(size, Integer.MAX_VALUE);
    }

    public int getSlots() {
        return fluids.length;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public FluidStack[] getFluids() {
        return fluids;
    }

    @Nonnull
    public FluidStack getFluid(int slot) {
        return fluids[slot];
    }

    public void setFluid(int slot, @Nonnull FluidStack stack) {
        if (stack.getAmount() > maxAmount) {
            throw new IllegalArgumentException("Fluid size is invalid (given: " + stack.getAmount() + ", max size: " + maxAmount + ")");
        }

        fluids[slot] = stack;

        onContentsChanged();
    }

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = new CompoundNBT();

        for (int i = 0; i < getSlots(); ++i) {
            FluidStack stack = getFluid(i);

            if (!stack.isEmpty()) {
                tag.put(String.format(NBT_SLOT, i), stack.writeToNBT(new CompoundNBT()));
            }
        }

        return tag;
    }

    public void readFromNbt(CompoundNBT tag) {
        for (int i = 0; i < getSlots(); ++i) {
            String key = String.format(NBT_SLOT, i);

            if (tag.contains(key)) {
                fluids[i] = FluidStack.loadFluidStackFromNBT(tag.getCompound(key));
            }
        }
    }

    protected void onContentsChanged() {

    }
}
