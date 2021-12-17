package com.refinedmods.refinedpipes.container;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.container.slot.FilterSlot;
import com.refinedmods.refinedpipes.container.slot.FluidFilterSlot;
import com.refinedmods.refinedpipes.message.FluidFilterSlotUpdateMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BaseContainerMenu extends AbstractContainerMenu {
    private final List<FluidFilterSlot> fluidSlots = new ArrayList<>();
    private final List<FluidStack> fluids = new ArrayList<>();
    private final Player player;

    protected BaseContainerMenu(@Nullable MenuType<?> type, int windowId, Player player) {
        super(type, windowId);

        this.player = player;
    }

    protected void addPlayerInventory(int xInventory, int yInventory) {
        int id = 9;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(player.getInventory(), id, xInventory + x * 18, yInventory + y * 18));

                id++;
            }
        }

        id = 0;

        for (int i = 0; i < 9; i++) {
            int x = xInventory + i * 18;
            int y = yInventory + 4 + (3 * 18);

            addSlot(new Slot(player.getInventory(), id, x, y));

            id++;
        }
    }

    @Override
    public void clicked(int id, int dragType, ClickType clickType, Player player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        ItemStack holding = player.containerMenu.getCarried();

        if (slot instanceof FilterSlot) {
            if (holding.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else if (slot.mayPlace(holding)) {
                slot.set(holding.copy());
            }

            return;
        } else if (slot instanceof FluidFilterSlot) {
            if (holding.isEmpty()) {
                ((FluidFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
            } else {
                ((FluidFilterSlot) slot).onContainerClicked(holding);
            }

            return;
        }

        super.clicked(id, dragType, clickType, player);
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
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!(player instanceof ServerPlayer)) {
            return;
        }

        for (int i = 0; i < this.fluidSlots.size(); ++i) {
            FluidFilterSlot slot = this.fluidSlots.get(i);

            FluidStack cached = this.fluids.get(i);
            FluidStack actual = slot.getFluidInventory().getFluid(slot.getSlotIndex());

            if (!cached.equals(actual)) {
                this.fluids.set(i, actual.copy());

                RefinedPipes.NETWORK.sendToClient((ServerPlayer) player, new FluidFilterSlotUpdateMessage(slot.getInventoryIndex(), actual));
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public List<FluidFilterSlot> getFluidSlots() {
        return fluidSlots;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (slot instanceof FilterSlot || slot instanceof FluidFilterSlot) {
            return false;
        }

        return super.canTakeItemForPickAll(stack, slot);
    }
}
