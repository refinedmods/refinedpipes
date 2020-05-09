package com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor;

import com.raoulvdberge.refinedpipes.container.provider.ExtractorAttachmentContainerProvider;
import com.raoulvdberge.refinedpipes.inventory.fluid.FluidInventory;
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
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ExtractorAttachment extends Attachment {
    private static final Logger LOGGER = LogManager.getLogger(ExtractorAttachment.class);

    public static final int MAX_FILTER_SLOTS = 15;

    private final ExtractorAttachmentType type;
    private final ItemStackHandler itemFilter;
    private final FluidInventory fluidFilter;

    private final ItemDestinationFinder itemDestinationFinder = new ItemDestinationFinder(this);

    private int ticks;
    private RedstoneMode redstoneMode = RedstoneMode.IGNORED;
    private BlacklistWhitelist blacklistWhitelist = BlacklistWhitelist.BLACKLIST;
    private RoutingMode routingMode = RoutingMode.NEAREST;
    private int stackSize;
    private boolean exactMode = true;

    public ExtractorAttachment(Pipe pipe, Direction direction, ExtractorAttachmentType type) {
        super(pipe, direction);

        this.type = type;
        this.stackSize = type.getItemsToExtract();
        this.itemFilter = createItemFilterInventory(this);
        this.fluidFilter = createFluidFilterInventory(this);
    }

    public boolean isFluidMode() {
        return pipe.getNetwork() instanceof FluidNetwork;
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
        if (stackSize == 0) {
            return;
        }

        int firstSlot = getSlotToExtractFrom(source);
        if (firstSlot == -1) {
            return;
        }

        ItemStack extracted = source.extractItem(firstSlot, stackSize, true);
        if (extracted.isEmpty()) {
            return;
        }

        Destination destination = itemDestinationFinder.find(routingMode, sourcePos, extracted);
        if (destination == null) {
            return;
        }

        Path<BlockPos> path = network
            .getDestinationPathCache()
            .getPath(pipe.getPos(), destination);
        if (path == null) {
            LOGGER.error("No path found from " + pipe.getPos() + " to " + destination);
            return;
        }

        ItemStack extractedActual = source.extractItem(firstSlot, stackSize, false);
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

    private void update(FluidNetwork network, IFluidHandler source) {
        FluidStack drained = source.drain(type.getFluidsToExtract(), IFluidHandler.FluidAction.SIMULATE);
        if (drained.isEmpty()) {
            return;
        }

        if (!acceptsFluid(drained)) {
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

    private int getSlotToExtractFrom(IItemHandler handler) {
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

                boolean equals = filtered.isItemEqual(stack);
                if (exactMode) {
                    equals = equals && ItemStack.areItemStackTagsEqual(filtered, stack);
                }

                if (equals) {
                    return true;
                }
            }

            return false;
        } else if (blacklistWhitelist == BlacklistWhitelist.BLACKLIST) {
            for (int i = 0; i < itemFilter.getSlots(); ++i) {
                ItemStack filtered = itemFilter.getStackInSlot(i);

                boolean equals = filtered.isItemEqual(stack);
                if (exactMode) {
                    equals = equals && ItemStack.areItemStackTagsEqual(filtered, stack);
                }

                if (equals) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    private boolean acceptsFluid(FluidStack stack) {
        if (blacklistWhitelist == BlacklistWhitelist.WHITELIST) {
            for (int i = 0; i < fluidFilter.getSlots(); ++i) {
                FluidStack filtered = fluidFilter.getFluid(i);

                boolean equals = filtered.getFluid() == stack.getFluid();
                if (exactMode) {
                    equals = equals && FluidStack.areFluidStackTagsEqual(filtered, stack);
                }

                if (equals) {
                    return true;
                }
            }

            return false;
        } else if (blacklistWhitelist == BlacklistWhitelist.BLACKLIST) {
            for (int i = 0; i < fluidFilter.getSlots(); ++i) {
                FluidStack filtered = fluidFilter.getFluid(i);

                boolean equals = filtered.getFluid() == stack.getFluid();
                if (exactMode) {
                    equals = equals && FluidStack.areFluidStackTagsEqual(filtered, stack);
                }

                if (equals) {
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

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putByte("rm", (byte) redstoneMode.ordinal());
        tag.put("itemfilter", itemFilter.serializeNBT());
        tag.putByte("bw", (byte) blacklistWhitelist.ordinal());
        tag.putInt("rr", itemDestinationFinder.getRoundRobinIndex());
        tag.putByte("routingm", (byte) routingMode.ordinal());
        tag.putInt("stacksi", stackSize);
        tag.putBoolean("exa", exactMode);
        tag.put("fluidfilter", fluidFilter.writeToNbt());

        return super.writeToNbt(tag);
    }

    public ExtractorAttachmentType getType() {
        return type;
    }

    public ItemStackHandler getItemFilter() {
        return itemFilter;
    }

    public FluidInventory getFluidFilter() {
        return fluidFilter;
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        if (!type.getCanSetRedstoneMode()) {
            return;
        }

        this.redstoneMode = redstoneMode;
    }

    public void setBlacklistWhitelist(BlacklistWhitelist blacklistWhitelist) {
        if (!type.getCanSetWhitelistBlacklist()) {
            return;
        }

        this.blacklistWhitelist = blacklistWhitelist;
    }

    public BlacklistWhitelist getBlacklistWhitelist() {
        return blacklistWhitelist;
    }

    public void setRoutingMode(RoutingMode routingMode) {
        if (!type.getCanSetRoutingMode()) {
            return;
        }

        this.routingMode = routingMode;
    }

    public RoutingMode getRoutingMode() {
        return routingMode;
    }

    public void setStackSize(int stackSize) {
        if (stackSize < 0) {
            stackSize = 0;
        }

        if (stackSize > type.getItemsToExtract()) {
            stackSize = type.getItemsToExtract();
        }

        this.stackSize = stackSize;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void setRoundRobinIndex(int roundRobinIndex) {
        itemDestinationFinder.setRoundRobinIndex(roundRobinIndex);
    }

    public void setExactMode(boolean exactMode) {
        if (!type.getCanSetExactMode()) {
            return;
        }

        this.exactMode = exactMode;
    }

    public boolean isExactMode() {
        return exactMode;
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

    public static FluidInventory createFluidFilterInventory(@Nullable ExtractorAttachment attachment) {
        return new FluidInventory(MAX_FILTER_SLOTS) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();

                if (attachment != null) {
                    NetworkManager.get(attachment.pipe.getWorld()).markDirty();
                }
            }
        };
    }
}
