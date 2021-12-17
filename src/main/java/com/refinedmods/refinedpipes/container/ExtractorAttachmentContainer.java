package com.refinedmods.refinedpipes.container;

import com.refinedmods.refinedpipes.RefinedPipes;
import com.refinedmods.refinedpipes.RefinedPipesContainers;
import com.refinedmods.refinedpipes.container.slot.FilterSlot;
import com.refinedmods.refinedpipes.container.slot.FluidFilterSlot;
import com.refinedmods.refinedpipes.inventory.fluid.FluidInventory;
import com.refinedmods.refinedpipes.message.*;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.BlacklistWhitelist;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachmentType;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.RedstoneMode;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.RoutingMode;
import com.refinedmods.refinedpipes.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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
        Player player,
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

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;

        RefinedPipes.NETWORK.sendToServer(new ChangeRedstoneModeMessage(pos, dir, redstoneMode));
    }

    public BlacklistWhitelist getBlacklistWhitelist() {
        return blacklistWhitelist;
    }

    public void setBlacklistWhitelist(BlacklistWhitelist blacklistWhitelist) {
        this.blacklistWhitelist = blacklistWhitelist;

        RefinedPipes.NETWORK.sendToServer(new ChangeBlacklistWhitelistMessage(pos, dir, blacklistWhitelist));
    }

    public RoutingMode getRoutingMode() {
        return routingMode;
    }

    public void setRoutingMode(RoutingMode routingMode) {
        this.routingMode = routingMode;

        RefinedPipes.NETWORK.sendToServer(new ChangeRoutingModeMessage(pos, dir, routingMode));
    }

    public int getStackSize() {
        return stackSize;
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;

        RefinedPipes.NETWORK.sendToServer(new ChangeStackSizeMessage(pos, dir, stackSize));
    }

    public boolean isExactMode() {
        return exactMode;
    }

    public void setExactMode(boolean exactMode) {
        this.exactMode = exactMode;

        RefinedPipes.NETWORK.sendToServer(new ChangeExactModeMessage(pos, dir, exactMode));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem() && index < 9 * 4) {
            for (int i = 9 * 4; i < slots.size(); ++i) {
                Slot filterSlot = slots.get(i);

                if (filterSlot instanceof FluidFilterSlot) {
                    FluidFilterSlot fluidSlot = (FluidFilterSlot) filterSlot;

                    if (fluidSlot.getFluidInventory().getFluid(fluidSlot.getSlotIndex()).isEmpty()) {
                        FluidStack toInsert = FluidUtil.getFromStack(slot.getItem(), true).getValue();

                        boolean foundExistingFluid = false;

                        for (int j = 0; j < fluidSlot.getFluidInventory().getSlots(); ++j) {
                            if (fluidSlot.getFluidInventory().getFluid(j).isFluidEqual(toInsert)) {
                                foundExistingFluid = true;
                                break;
                            }
                        }

                        if (!foundExistingFluid) {
                            fluidSlot.onContainerClicked(slot.getItem());
                        }

                        break;
                    }
                } else if (filterSlot instanceof SlotItemHandler) {
                    SlotItemHandler itemSlot = (SlotItemHandler) filterSlot;

                    if (!itemSlot.hasItem()) {
                        ItemStack toInsert = ItemHandlerHelper.copyStackWithSize(slot.getItem(), 1);

                        boolean foundExistingItem = false;

                        for (int j = 0; j < itemSlot.getItemHandler().getSlots(); ++j) {
                            if (ItemStack.matches(itemSlot.getItemHandler().getStackInSlot(j), toInsert)) {
                                foundExistingItem = true;
                                break;
                            }
                        }

                        if (!foundExistingItem) {
                            itemSlot.set(toInsert);
                        }

                        break;
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }
}
