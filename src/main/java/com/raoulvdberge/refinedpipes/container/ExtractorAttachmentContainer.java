package com.raoulvdberge.refinedpipes.container;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesContainers;
import com.raoulvdberge.refinedpipes.container.slot.FilterSlot;
import com.raoulvdberge.refinedpipes.container.slot.FluidFilterSlot;
import com.raoulvdberge.refinedpipes.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedpipes.message.*;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.BlacklistWhitelist;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.RedstoneMode;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.RoutingMode;
import com.raoulvdberge.refinedpipes.util.FluidUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ExtractorAttachmentContainer extends BaseContainer {
    private final BlockPos pos;
    private final Direction dir;
    private final ExtractorAttachmentType extractorAttachmentType;
    private final boolean fluidMode;

    private RedstoneMode redstoneMode;
    private BlacklistWhitelist blacklistWhitelist;
    private RoutingMode routingMode;
    private int stackSize;
    private boolean exactMode;

    public ExtractorAttachmentContainer(
        int windowId,
        PlayerEntity player,
        BlockPos pos,
        Direction dir,
        RedstoneMode redstoneMode,
        BlacklistWhitelist blacklistWhitelist,
        RoutingMode routingMode,
        int stackSize,
        boolean exactMode,
        ExtractorAttachmentType type,
        ItemStackHandler itemFilter,
        FluidInventory fluidFilter,
        boolean fluidMode) {
        super(RefinedPipesContainers.EXTRACTOR_ATTACHMENT, windowId, player);

        addPlayerInventory(8, 111);

        int x = 44;
        int y = 19;
        for (int i = 1; i <= type.getFilterSlots(); ++i) {
            if (fluidMode) {
                addSlot(new FluidFilterSlot(fluidFilter, i - 1, x, y));
            } else {
                addSlot(new FilterSlot(itemFilter, i - 1, x, y));
            }

            if (i % 5 == 0) {
                x = 44;
                y += 18;
            } else {
                x += 18;
            }
        }

        this.pos = pos;
        this.dir = dir;
        this.extractorAttachmentType = type;
        this.fluidMode = fluidMode;

        this.redstoneMode = redstoneMode;
        this.blacklistWhitelist = blacklistWhitelist;
        this.routingMode = routingMode;
        this.stackSize = stackSize;
        this.exactMode = exactMode;
    }

    public boolean isFluidMode() {
        return fluidMode;
    }

    public ExtractorAttachmentType getExtractorAttachmentType() {
        return extractorAttachmentType;
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public BlacklistWhitelist getBlacklistWhitelist() {
        return blacklistWhitelist;
    }

    public RoutingMode getRoutingMode() {
        return routingMode;
    }

    public int getStackSize() {
        return stackSize;
    }

    public boolean isExactMode() {
        return exactMode;
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;

        RefinedPipes.NETWORK.sendToServer(new ChangeRedstoneModeMessage(pos, dir, redstoneMode));
    }

    public void setBlacklistWhitelist(BlacklistWhitelist blacklistWhitelist) {
        this.blacklistWhitelist = blacklistWhitelist;

        RefinedPipes.NETWORK.sendToServer(new ChangeBlacklistWhitelistMessage(pos, dir, blacklistWhitelist));
    }

    public void setRoutingMode(RoutingMode routingMode) {
        this.routingMode = routingMode;

        RefinedPipes.NETWORK.sendToServer(new ChangeRoutingModeMessage(pos, dir, routingMode));
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;

        RefinedPipes.NETWORK.sendToServer(new ChangeStackSizeMessage(pos, dir, stackSize));
    }

    public void setExactMode(boolean exactMode) {
        this.exactMode = exactMode;

        RefinedPipes.NETWORK.sendToServer(new ChangeExactModeMessage(pos, dir, exactMode));
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack() && index < 9 * 4) {
            for (int i = 9 * 4; i < inventorySlots.size(); ++i) {
                Slot filterSlot = inventorySlots.get(i);

                if (filterSlot instanceof FluidFilterSlot) {
                    FluidFilterSlot fluidSlot = (FluidFilterSlot) filterSlot;

                    if (fluidSlot.getFluidInventory().getFluid(fluidSlot.getSlotIndex()).isEmpty()) {
                        FluidStack toInsert = FluidUtil.getFromStack(slot.getStack(), true).getValue();

                        boolean foundExistingFluid = false;

                        for (int j = 0; j < fluidSlot.getFluidInventory().getSlots(); ++j) {
                            if (fluidSlot.getFluidInventory().getFluid(j).isFluidEqual(toInsert)) {
                                foundExistingFluid = true;
                                break;
                            }
                        }

                        if (!foundExistingFluid) {
                            fluidSlot.onContainerClicked(slot.getStack());
                        }

                        break;
                    }
                } else if (filterSlot instanceof SlotItemHandler) {
                    SlotItemHandler itemSlot = (SlotItemHandler) filterSlot;

                    if (!itemSlot.getHasStack()) {
                        ItemStack toInsert = ItemHandlerHelper.copyStackWithSize(slot.getStack(), 1);

                        boolean foundExistingItem = false;

                        for (int j = 0; j < itemSlot.getItemHandler().getSlots(); ++j) {
                            if (ItemStack.areItemStacksEqual(itemSlot.getItemHandler().getStackInSlot(j), toInsert)) {
                                foundExistingItem = true;
                                break;
                            }
                        }

                        if (!foundExistingItem) {
                            itemSlot.putStack(toInsert);
                        }

                        break;
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }
}
