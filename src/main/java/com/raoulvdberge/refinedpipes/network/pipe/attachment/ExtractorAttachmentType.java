package com.raoulvdberge.refinedpipes.network.pipe.attachment;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesItems;
import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemBounceBackTransportCallback;
import com.raoulvdberge.refinedpipes.network.pipe.transport.callback.ItemInsertTransportCallback;
import com.raoulvdberge.refinedpipes.network.route.Path;
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

    private static final ResourceLocation MODEL_LOCATION = new ResourceLocation(RefinedPipes.ID, "block/pipe/attachment/extractor_attachment");
    private static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "extractor");
    private static final ResourceLocation ITEM_ID = new ResourceLocation(RefinedPipes.ID, "extractor_attachment");

    @Override
    public ResourceLocation getModelLocation() {
        return MODEL_LOCATION;
    }

    @Override
    public void update(World world, Network network, Pipe pipe, Attachment attachment) {
        BlockPos itemHandlerPos = pipe.getPos().offset(attachment.getDirection());

        TileEntity tile = world.getTileEntity(itemHandlerPos);
        if (tile == null) {
            return;
        }

        tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, attachment.getDirection())
            .ifPresent(itemHandler -> update(network, pipe, attachment, itemHandlerPos, itemHandler));
    }

    private void update(Network network, Pipe pipe, Attachment attachment, BlockPos itemHandlerPos, IItemHandler itemHandler) {
        int firstSlot = getFirstSlot(itemHandler);
        if (firstSlot == -1) {
            return;
        }

        ItemStack extracted = itemHandler.extractItem(firstSlot, 1, true);
        if (extracted.isEmpty()) {
            return;
        }

        Destination<IItemHandler> destination = network
            .getGraph()
            .getDestinationPathCache()
            .findNearestDestination(
                pipe.getPos(),
                dest -> dest.getDest() != itemHandler && ItemHandlerHelper.insertItem(dest.getDest(), extracted, true).isEmpty()
            );

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

        ItemStack extractedActual = itemHandler.extractItem(firstSlot, 1, false);
        if (extractedActual.isEmpty()) {
            return;
        }

        BlockPos fromPos = pipe.getPos().offset(attachment.getDirection());

        pipe.addTransport(new ItemTransport(
            extractedActual.copy(),
            fromPos,
            destination.getDestPos(),
            path.toQueue(),
            new ItemInsertTransportCallback(destination.getDestPos(), extractedActual),
            new ItemBounceBackTransportCallback(destination.getDestPos(), itemHandlerPos, extractedActual)
        ));
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
        return ID;
    }

    @Override
    public ResourceLocation getItemId() {
        return ITEM_ID;
    }

    @Override
    public ItemStack toStack() {
        return new ItemStack(RefinedPipesItems.EXTRACTOR_ATTACHMENT);
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
}
