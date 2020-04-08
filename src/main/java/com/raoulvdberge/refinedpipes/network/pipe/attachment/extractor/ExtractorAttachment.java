package com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor;

import com.raoulvdberge.refinedpipes.container.provider.ExtractorAttachmentContainerProvider;
import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.fluid.FluidNetwork;
import com.raoulvdberge.refinedpipes.network.item.ItemNetwork;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipe;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemBounceBackTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemInsertTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemPipeGoneTransportCallback;
import com.raoulvdberge.refinedpipes.routing.Path;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ExtractorAttachment extends Attachment {
    private static final Logger LOGGER = LogManager.getLogger(ExtractorAttachment.class);

    public static final int MAX_FILTER_SLOTS = 15;

    private final Pipe pipe;
    private final ExtractorAttachmentType type;

    private int ticks;
    private RedstoneMode redstoneMode = RedstoneMode.IGNORED;
    private BlacklistWhitelist blacklistWhitelist = BlacklistWhitelist.BLACKLIST;
    private ItemStackHandler itemFilter;

    public ExtractorAttachment(Pipe pipe, Direction direction, ExtractorAttachmentType type) {
        super(direction);

        this.pipe = pipe;
        this.type = type;
        this.itemFilter = createItemFilterInventory(this);
    }

    @Override
    public void update() {
        Network network = pipe.getNetwork();

        int tickInterval = 0;
        if (network instanceof ItemNetwork) {
            tickInterval = type.getItemTickInterval();
        } else if (network instanceof FluidNetwork) {
            tickInterval = type.getFluidTickInterval();
        }

        if (tickInterval != 0 && (ticks++) % tickInterval != 0) {
            return;
        }

        if (!redstoneMode.isEnabled(pipe.getWorld(), pipe.getPos())) {
            return;
        }

        BlockPos destinationPos = pipe.getPos().offset(getDirection());

        TileEntity tile = pipe.getWorld().getTileEntity(destinationPos);
        if (tile == null) {
            return;
        }

        if (network instanceof ItemNetwork) {
            tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getDirection().getOpposite())
                .ifPresent(itemHandler -> update((ItemNetwork) network, destinationPos, itemHandler));
        } else if (network instanceof FluidNetwork) {
            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getDirection().getOpposite())
                .ifPresent(fluidHandler -> update((FluidNetwork) network, fluidHandler));
        }
    }

    private void update(ItemNetwork network, BlockPos sourcePos, IItemHandler source) {
        int firstSlot = getFirstSlot(source);
        if (firstSlot == -1) {
            return;
        }

        ItemStack extracted = source.extractItem(firstSlot, type.getItemsToExtract(), true);
        if (extracted.isEmpty()) {
            return;
        }

        Destination destination = network
            .getDestinationPathCache()
            .findNearestDestination(pipe.getPos(), d -> isDestinationApplicable(sourcePos, extracted, d));

        if (destination == null) {
            LOGGER.warn("No destination found from " + pipe.getPos());
            return;
        }

        Path<BlockPos> path = network
            .getDestinationPathCache()
            .getPath(pipe.getPos(), destination);

        if (path == null) {
            LOGGER.error("No path found from " + pipe.getPos() + " to " + destination);
            return;
        }

        ItemStack extractedActual = source.extractItem(firstSlot, type.getItemsToExtract(), false);
        if (extractedActual.isEmpty()) {
            return;
        }

        BlockPos fromPos = pipe.getPos().offset(getDirection());

        ((ItemPipe) pipe).addTransport(new ItemTransport(
            extractedActual.copy(),
            fromPos,
            destination.getReceiver(),
            path.toQueue(),
            new ItemInsertTransportCallback(destination.getReceiver(), destination.getIncomingDirection(), extractedActual),
            new ItemBounceBackTransportCallback(destination.getReceiver(), sourcePos, extractedActual),
            new ItemPipeGoneTransportCallback(extractedActual)
        ));
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;
    }

    public void setBlacklistWhitelist(BlacklistWhitelist blacklistWhitelist) {
        this.blacklistWhitelist = blacklistWhitelist;
    }

    private void update(FluidNetwork network, IFluidHandler source) {
        FluidStack drained = source.drain(type.getFluidsToExtract(), IFluidHandler.FluidAction.SIMULATE);
        if (drained.isEmpty()) {
            return;
        }

        int filled = network.getFluidTank().fill(drained, IFluidHandler.FluidAction.SIMULATE);
        if (filled <= 0) {
            return;
        }

        int toDrain = Math.min(type.getFluidsToExtract(), filled);

        drained = source.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);

        network.getFluidTank().fill(drained, IFluidHandler.FluidAction.EXECUTE);

        NetworkManager.get(pipe.getWorld()).markDirty();
    }

    private boolean isDestinationApplicable(BlockPos sourcePos, ItemStack extracted, Destination destination) {
        TileEntity tile = destination.getConnectedPipe().getWorld().getTileEntity(destination.getReceiver());
        if (tile == null) {
            return false;
        }

        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, destination.getIncomingDirection().getOpposite()).orElse(null);
        if (handler == null) {
            return false;
        }

        // Avoid extractions that lead back to the source pos through the same pipe.
        // Only if the incoming direction is different, then we'll allow it.
        if (destination.getReceiver().equals(sourcePos) && destination.getIncomingDirection() == getDirection()) {
            return false;
        }

        return ItemHandlerHelper.insertItem(handler, extracted, true).isEmpty();
    }

    private int getFirstSlot(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);

            if (!stack.isEmpty() && acceptsItem(stack)) {
                return i;
            }
        }

        return -1;
    }

    private boolean acceptsItem(ItemStack stack) {
        if (blacklistWhitelist == BlacklistWhitelist.WHITELIST) {
            for (int i = 0; i < itemFilter.getSlots(); ++i) {
                ItemStack filtered = itemFilter.getStackInSlot(i);

                if (filtered.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(filtered, stack)) {
                    return true;
                }
            }

            return false;
        } else if (blacklistWhitelist == BlacklistWhitelist.BLACKLIST) {
            for (int i = 0; i < itemFilter.getSlots(); ++i) {
                ItemStack filtered = itemFilter.getStackInSlot(i);

                if (filtered.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(filtered, stack)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void openContainer(ServerPlayerEntity player) {
        super.openContainer(player);

        ExtractorAttachmentContainerProvider.open(pipe, this, player);
    }

    @Override
    public ResourceLocation getId() {
        return type.getId();
    }

    @Override
    public ItemStack getDrop() {
        return new ItemStack(type.getItem());
    }

    public ExtractorAttachmentType getType() {
        return type;
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public BlacklistWhitelist getBlacklistWhitelist() {
        return blacklistWhitelist;
    }

    public ItemStackHandler getItemFilter() {
        return itemFilter;
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putByte("rm", (byte) redstoneMode.ordinal());
        tag.put("itemfilter", itemFilter.serializeNBT());
        tag.putByte("bw", (byte) blacklistWhitelist.ordinal());

        return super.writeToNbt(tag);
    }

    public static ItemStackHandler createItemFilterInventory(@Nullable ExtractorAttachment attachment) {
        return new ItemStackHandler(MAX_FILTER_SLOTS) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);

                if (attachment != null) {
                    NetworkManager.get(attachment.pipe.getWorld()).markDirty();
                }
            }
        };
    }
}
