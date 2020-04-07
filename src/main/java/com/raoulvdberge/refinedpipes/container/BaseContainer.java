package com.raoulvdberge.refinedpipes.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;

import javax.annotation.Nullable;

public class BaseContainer extends Container {
    protected BaseContainer(@Nullable ContainerType<?> type, int windowId) {
        super(type, windowId);
    }

    protected void addPlayerInventory(PlayerEntity player, int xInventory, int yInventory) {
        int id = 0;

        for (int i = 0; i < 9; i++) {
            int x = xInventory + i * 18;
            int y = yInventory + 4 + (3 * 18);

            addSlot(new Slot(player.inventory, id, x, y));

            id++;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18));

                id++;
            }
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }
}
