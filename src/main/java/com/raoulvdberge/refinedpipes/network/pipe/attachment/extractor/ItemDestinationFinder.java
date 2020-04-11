package com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor;

import com.raoulvdberge.refinedpipes.network.item.ItemNetwork;
import com.raoulvdberge.refinedpipes.network.pipe.Destination;
import com.raoulvdberge.refinedpipes.network.pipe.DestinationType;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemDestinationFinder {
    private final Attachment attachment;
    private int roundRobinIndex;

    public ItemDestinationFinder(Attachment attachment) {
        this.attachment = attachment;
    }

    public Destination find(RoutingMode routingMode, BlockPos sourcePos, ItemStack extracted) {
        ItemNetwork network = (ItemNetwork) attachment.getPipe().getNetwork();

        switch (routingMode) {
            case NEAREST:
                return network.getDestinationPathCache()
                    .findNearestDestination(attachment.getPipe().getPos(), d -> isDestinationApplicable(sourcePos, extracted, d));
            case FURTHEST:
                return network.getDestinationPathCache()
                    .findFurthestDestination(attachment.getPipe().getPos(), d -> isDestinationApplicable(sourcePos, extracted, d));
            case RANDOM: {
                List<Destination> destinations = new ArrayList<>(network.getDestinations(DestinationType.ITEM_HANDLER));

                while (!destinations.isEmpty()) {
                    int randomIndex = attachment.getPipe().getWorld().getRandom().nextInt(destinations.size());
                    Destination randomDestination = destinations.get(randomIndex);

                    if (isDestinationApplicable(sourcePos, extracted, randomDestination)) {
                        return randomDestination;
                    }

                    destinations.remove(randomIndex);
                }

                return null;
            }
            case ROUND_ROBIN: {
                List<Destination> destinations = network.getDestinations(DestinationType.ITEM_HANDLER);
                if (roundRobinIndex >= destinations.size()) {
                    roundRobinIndex = 0;
                }

                while (true) {
                    Destination dest = destinations.get(roundRobinIndex);

                    if (isDestinationApplicable(sourcePos, extracted, dest)) {
                        roundRobinIndex++;
                        return dest;
                    } else {
                        roundRobinIndex++;
                        if (roundRobinIndex >= destinations.size()) {
                            break;
                        }
                    }
                }

                return null;
            }
            default:
                throw new RuntimeException("?");
        }
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
        if (destination.getReceiver().equals(sourcePos) && destination.getIncomingDirection() == attachment.getDirection()) {
            return false;
        }

        return ItemHandlerHelper.insertItem(handler, extracted, true).isEmpty();
    }

    public int getRoundRobinIndex() {
        return roundRobinIndex;
    }

    public void setRoundRobinIndex(int roundRobinIndex) {
        this.roundRobinIndex = roundRobinIndex;
    }
}
