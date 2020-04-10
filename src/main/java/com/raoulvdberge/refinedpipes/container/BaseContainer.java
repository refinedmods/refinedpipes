package com.raoulvdberge.refinedpipes.container;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.container.slot.FilterSlot;
import com.raoulvdberge.refinedpipes.container.slot.FluidFilterSlot;
import com.raoulvdberge.refinedpipes.message.FluidFilterSlotUpdateMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BaseContainer extends Container {
    private final List<FluidFilterSlot> fluidSlots = new ArrayList<>();
    private final List<FluidStack> fluids = new ArrayList<>();
    private final PlayerEntity player;

    protected BaseContainer(@Nullable ContainerType<?> type, int windowId, PlayerEntity player) {
        super(type, windowId);

        this.player = player;
    }

    protected void addPlayerInventory(int xInventory, int yInventory) {
        int id = 9;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18));

                id++;
            }
        }

        id = 0;

        for (int i = 0; i < 9; i++) {
            int x = xInventory + i * 18;
            int y = yInventory + 4 + (3 * 18);

            addSlot(new Slot(player.inventory, id, x, y));

            id++;
        }
    }

    @Override
    public ItemStack slotClick(int id, int dragType, ClickType clickType, PlayerEntity player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        ItemStack holding = player.inventory.getItemStack();

        if (slot instanceof FilterSlot) {
            if (holding.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else if (slot.isItemValid(holding)) {
                slot.putStack(holding.copy());
            }

            return holding;
        } else if (slot instanceof FluidFilterSlot) {
            if (holding.isEmpty()) {
                ((FluidFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
            } else {
                ((FluidFilterSlot) slot).onContainerClicked(holding);
            }

            return holding;
        }

        return super.slotClick(id, dragType, clickType, player);
    }

    @Override
    protected Slot addSlot(Slot slot) {
        if (slot instanceof FluidFilterSlot) {
            fluids.add(FluidStack.EMPTY);
            fluidSlots.add((FluidFilterSlot) slot);
        }

        return super.addSlot(slot);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }

        for (int i = 0; i < this.fluidSlots.size(); ++i) {
            FluidFilterSlot slot = this.fluidSlots.get(i);

            FluidStack cached = this.fluids.get(i);
            FluidStack actual = slot.getFluidInventory().getFluid(slot.getSlotIndex());

            if (!cached.equals(actual)) {
                this.fluids.set(i, actual.copy());

                RefinedPipes.NETWORK.sendToClient((ServerPlayerEntity) player, new FluidFilterSlotUpdateMessage(slot.slotNumber, actual));
            }
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }

    public List<FluidFilterSlot> getFluidSlots() {
        return fluidSlots;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        if (slot instanceof FilterSlot || slot instanceof FluidFilterSlot) {
            return false;
        }

        return super.canMergeSlot(stack, slot);
    }
}
