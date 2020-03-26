package com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesItems;
import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.ItemPipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemBounceBackTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemInsertTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemPipeGoneTransportCallback;
import com.raoulvdberge.refinedpipes.network.route.Path;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtractorAttachmentType implements AttachmentType {
    private static final Logger LOGGER = LogManager.getLogger(ExtractorAttachmentType.class);

    private final Type type;

    public ExtractorAttachmentType(Type type) {
        this.type = type;
    }

    @Override
    public ResourceLocation getModelLocation() {
        return type.getModelLocation();
    }

    @Override
    public void update(World world, Network network, ItemPipe pipe, Attachment attachment, int ticks) {
        if (ticks % type.tickInterval != 0) {
            return;
        }

        BlockPos itemHandlerPos = pipe.getPos().offset(attachment.getDirection());

        TileEntity tile = world.getTileEntity(itemHandlerPos);
        if (tile == null) {
            return;
        }

        tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, attachment.getDirection())
            .ifPresent(itemHandler -> update(network, pipe, attachment, itemHandlerPos, itemHandler));
    }

    private void update(Network network, ItemPipe pipe, Attachment attachment, BlockPos sourcePos, IItemHandler source) {
        int firstSlot = getFirstSlot(source);
        if (firstSlot == -1) {
            return;
        }

        ItemStack extracted = source.extractItem(firstSlot, type.itemsToExtract, true);
        if (extracted.isEmpty()) {
            return;
        }

        Destination destination = network
            .getGraph()
            .getDestinationPathCache()
            .findNearestDestination(pipe.getPos(), d -> isDestinationApplicable(attachment, sourcePos, extracted, d));

        if (destination == null) {
            LOGGER.error("No destination found from " + pipe.getPos());
            return;
        }

        Path<BlockPos> path = network
            .getGraph()
            .getDestinationPathCache()
            .getPath(pipe.getPos(), destination);

        if (path == null) {
            LOGGER.error("No path found from " + pipe.getPos() + " to " + destination);
            return;
        }

        ItemStack extractedActual = source.extractItem(firstSlot, type.itemsToExtract, false);
        if (extractedActual.isEmpty()) {
            return;
        }

        BlockPos fromPos = pipe.getPos().offset(attachment.getDirection());

        pipe.addTransport(new ItemTransport(
            extractedActual.copy(),
            fromPos,
            destination.getReceiver(),
            path.toQueue(),
            new ItemInsertTransportCallback(destination.getReceiver(), destination.getIncomingDirection(), extractedActual),
            new ItemBounceBackTransportCallback(destination.getReceiver(), sourcePos, extractedActual),
            new ItemPipeGoneTransportCallback(extractedActual)
        ));
    }

    private boolean isDestinationApplicable(Attachment attachment, BlockPos sourcePos, ItemStack extracted, Destination destination) {
        TileEntity tile = destination.getConnectedPipe().getWorld().getTileEntity(destination.getReceiver());
        if (tile == null) {
            return false;
        }

        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, destination.getIncomingDirection()).orElse(null);
        if (handler == null) {
            return false;
        }

        // Avoid extractions that lead back to the source pos through the same pipe.
        // Only if the incoming direction is different, then we'll allow it.
        if (destination.getReceiver().equals(sourcePos) && destination.getIncomingDirection() == attachment.getDirection()) {
            return false;
        }

        return ItemHandlerHelper.insertItem(handler, extracted, true).isEmpty();
    }

    private int getFirstSlot(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public ResourceLocation getId() {
        return type.getId();
    }

    @Override
    public ResourceLocation getItemId() {
        return type.getItemId();
    }

    @Override
    public ItemStack toStack() {
        return new ItemStack(type.getItem());
    }

    @Override
    public Attachment createFromNbt(CompoundNBT tag) {
        Direction dir = Direction.values()[tag.getInt("dir")];

        return new Attachment(this, dir);
    }

    @Override
    public Attachment createNew(Direction dir) {
        return new Attachment(this, dir);
    }

    public enum Type {
        BASIC(20 * 3, 8),
        IMPROVED(20 * 2, 16),
        ADVANCED(20, 32),
        ELITE(10, 64),
        ULTIMATE(10, 64);

        private final int tickInterval;
        private final int itemsToExtract;

        Type(int tickInterval, int itemsToExtract) {
            this.tickInterval = tickInterval;
            this.itemsToExtract = itemsToExtract;
        }

        ResourceLocation getId() {
            switch (this) {
                case BASIC:
                    return new ResourceLocation(RefinedPipes.ID, "basic_extractor");
                case IMPROVED:
                    return new ResourceLocation(RefinedPipes.ID, "improved_extractor");
                case ADVANCED:
                    return new ResourceLocation(RefinedPipes.ID, "advanced_extractor");
                case ELITE:
                    return new ResourceLocation(RefinedPipes.ID, "elite_extractor");
                case ULTIMATE:
                    return new ResourceLocation(RefinedPipes.ID, "ultimate_extractor");
                default:
                    throw new RuntimeException("?");
            }
        }

        ResourceLocation getItemId() {
            switch (this) {
                case BASIC:
                    return new ResourceLocation(RefinedPipes.ID, "basic_extractor_attachment");
                case IMPROVED:
                    return new ResourceLocation(RefinedPipes.ID, "improved_extractor_attachment");
                case ADVANCED:
                    return new ResourceLocation(RefinedPipes.ID, "advanced_extractor_attachment");
                case ELITE:
                    return new ResourceLocation(RefinedPipes.ID, "elite_extractor_attachment");
                case ULTIMATE:
                    return new ResourceLocation(RefinedPipes.ID, "ultimate_extractor_attachment");
                default:
                    throw new RuntimeException("?");
            }
        }

        Item getItem() {
            switch (this) {
                case BASIC:
                    return RefinedPipesItems.BASIC_EXTRACTOR_ATTACHMENT;
                case IMPROVED:
                    return RefinedPipesItems.IMPROVED_EXTRACTOR_ATTACHMENT;
                case ADVANCED:
                    return RefinedPipesItems.ADVANCED_EXTRACTOR_ATTACHMENT;
                case ELITE:
                    return RefinedPipesItems.ELITE_EXTRACTOR_ATTACHMENT;
                case ULTIMATE:
                    return RefinedPipesItems.ULTIMATE_EXTRACTOR_ATTACHMENT;
                default:
                    throw new RuntimeException("?");
            }
        }

        ResourceLocation getModelLocation() {
            switch (this) {
                case BASIC:
                    return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/basic");
                case IMPROVED:
                    return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/improved");
                case ADVANCED:
                    return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/advanced");
                case ELITE:
                    return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/elite");
                case ULTIMATE:
                    return new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor/ultimate");
                default:
                    throw new RuntimeException("?");
            }
        }
    }
}
