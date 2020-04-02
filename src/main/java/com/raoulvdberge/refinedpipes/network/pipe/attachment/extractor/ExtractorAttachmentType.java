package com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesItems;
import com.raoulvdberge.refinedpipes.block.FluidPipeBlock;
import com.raoulvdberge.refinedpipes.block.ItemPipeBlock;
import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.fluid.FluidNetwork;
import com.raoulvdberge.refinedpipes.network.item.ItemNetwork;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.item.ItemPipe;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemBounceBackTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemInsertTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemPipeGoneTransportCallback;
import com.raoulvdberge.refinedpipes.routing.Path;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

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
    public void update(World world, Network network, Pipe pipe, Attachment attachment, int ticks) {
        int tickInterval = 0;
        if (network instanceof ItemNetwork) {
            tickInterval = type.getItemTickInterval();
        } else if (network instanceof FluidNetwork) {
            tickInterval = type.getFluidTickInterval();
        }

        if (tickInterval != 0 && ticks % tickInterval != 0) {
            return;
        }

        BlockPos destinationPos = pipe.getPos().offset(attachment.getDirection());

        TileEntity tile = world.getTileEntity(destinationPos);
        if (tile == null) {
            return;
        }

        if (network instanceof ItemNetwork) {
            tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, attachment.getDirection().getOpposite())
                .ifPresent(itemHandler -> update((ItemNetwork) network, pipe, attachment, destinationPos, itemHandler));
        } else if (network instanceof FluidNetwork) {
            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, attachment.getDirection().getOpposite())
                .ifPresent(fluidHandler -> update((FluidNetwork) network, world, fluidHandler));
        }
    }

    @Override
    public void addInformation(List<ITextComponent> tooltip) {
        tooltip.add(new TranslationTextComponent("misc.refinedpipes.tier", new TranslationTextComponent("enchantment.level." + type.tier)).setStyle(new Style().setColor(TextFormatting.YELLOW)));

        ITextComponent itemsToExtract = new StringTextComponent(type.getItemsToExtract() + " ")
            .appendSibling(new TranslationTextComponent("misc.refinedpipes.item" + (type.getItemsToExtract() == 1 ? "" : "s")))
            .setStyle(new Style().setColor(TextFormatting.WHITE));

        float itemSecondsInterval = type.getItemTickInterval() / 20F;
        ITextComponent itemTickInterval = new StringTextComponent(itemSecondsInterval + " ")
            .appendSibling(new TranslationTextComponent("misc.refinedpipes.second" + (itemSecondsInterval == 1 ? "" : "s")))
            .setStyle(new Style().setColor(TextFormatting.WHITE));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.extractor_attachment.item_extraction_rate",
            itemsToExtract,
            itemTickInterval
        ).setStyle(new Style().setColor(TextFormatting.GRAY)));

        ITextComponent fluidsToExtract = new StringTextComponent(type.getFluidsToExtract() + " mB")
            .setStyle(new Style().setColor(TextFormatting.WHITE));

        float fluidSecondsInterval = type.getFluidTickInterval() / 20F;
        ITextComponent fluidTickInterval = new StringTextComponent(fluidSecondsInterval + " ")
            .appendSibling(new TranslationTextComponent("misc.refinedpipes.second" + (fluidSecondsInterval == 1 ? "" : "s")))
            .setStyle(new Style().setColor(TextFormatting.WHITE));

        tooltip.add(new TranslationTextComponent(
            "tooltip.refinedpipes.extractor_attachment.fluid_extraction_rate",
            fluidsToExtract,
            fluidTickInterval
        ).setStyle(new Style().setColor(TextFormatting.GRAY)));
    }

    @Override
    public boolean canPlaceOnPipe(Block pipe) {
        return pipe instanceof ItemPipeBlock
            || pipe instanceof FluidPipeBlock;
    }

    private void update(ItemNetwork network, Pipe pipe, Attachment attachment, BlockPos sourcePos, IItemHandler source) {
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
            .findNearestDestination(pipe.getPos(), d -> isDestinationApplicable(attachment, sourcePos, extracted, d));

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

        BlockPos fromPos = pipe.getPos().offset(attachment.getDirection());

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

    private void update(FluidNetwork network, World world, IFluidHandler source) {
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

        NetworkManager.get(world).markDirty();
    }

    private boolean isDestinationApplicable(Attachment attachment, BlockPos sourcePos, ItemStack extracted, Destination destination) {
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
        BASIC(1),
        IMPROVED(2),
        ADVANCED(3),
        ELITE(4),
        ULTIMATE(5);

        private final int tier;

        Type(int tier) {
            this.tier = tier;
        }

        int getItemTickInterval() {
            switch (this) {
                case BASIC:
                    return RefinedPipes.SERVER_CONFIG.getBasicExtractorAttachment().getItemTickInterval();
                case IMPROVED:
                    return RefinedPipes.SERVER_CONFIG.getImprovedExtractorAttachment().getItemTickInterval();
                case ADVANCED:
                    return RefinedPipes.SERVER_CONFIG.getAdvancedExtractorAttachment().getItemTickInterval();
                case ELITE:
                    return RefinedPipes.SERVER_CONFIG.getEliteExtractorAttachment().getItemTickInterval();
                case ULTIMATE:
                    return RefinedPipes.SERVER_CONFIG.getUltimateExtractorAttachment().getItemTickInterval();
                default:
                    throw new RuntimeException("?");
            }
        }

        int getFluidTickInterval() {
            switch (this) {
                case BASIC:
                    return RefinedPipes.SERVER_CONFIG.getBasicExtractorAttachment().getFluidTickInterval();
                case IMPROVED:
                    return RefinedPipes.SERVER_CONFIG.getImprovedExtractorAttachment().getFluidTickInterval();
                case ADVANCED:
                    return RefinedPipes.SERVER_CONFIG.getAdvancedExtractorAttachment().getFluidTickInterval();
                case ELITE:
                    return RefinedPipes.SERVER_CONFIG.getEliteExtractorAttachment().getFluidTickInterval();
                case ULTIMATE:
                    return RefinedPipes.SERVER_CONFIG.getUltimateExtractorAttachment().getFluidTickInterval();
                default:
                    throw new RuntimeException("?");
            }
        }

        int getItemsToExtract() {
            switch (this) {
                case BASIC:
                    return RefinedPipes.SERVER_CONFIG.getBasicExtractorAttachment().getItemsToExtract();
                case IMPROVED:
                    return RefinedPipes.SERVER_CONFIG.getImprovedExtractorAttachment().getItemsToExtract();
                case ADVANCED:
                    return RefinedPipes.SERVER_CONFIG.getAdvancedExtractorAttachment().getItemsToExtract();
                case ELITE:
                    return RefinedPipes.SERVER_CONFIG.getEliteExtractorAttachment().getItemsToExtract();
                case ULTIMATE:
                    return RefinedPipes.SERVER_CONFIG.getUltimateExtractorAttachment().getItemsToExtract();
                default:
                    throw new RuntimeException("?");
            }
        }

        int getFluidsToExtract() {
            switch (this) {
                case BASIC:
                    return RefinedPipes.SERVER_CONFIG.getBasicExtractorAttachment().getFluidsToExtract();
                case IMPROVED:
                    return RefinedPipes.SERVER_CONFIG.getImprovedExtractorAttachment().getFluidsToExtract();
                case ADVANCED:
                    return RefinedPipes.SERVER_CONFIG.getAdvancedExtractorAttachment().getFluidsToExtract();
                case ELITE:
                    return RefinedPipes.SERVER_CONFIG.getEliteExtractorAttachment().getFluidsToExtract();
                case ULTIMATE:
                    return RefinedPipes.SERVER_CONFIG.getUltimateExtractorAttachment().getFluidsToExtract();
                default:
                    throw new RuntimeException("?");
            }
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
