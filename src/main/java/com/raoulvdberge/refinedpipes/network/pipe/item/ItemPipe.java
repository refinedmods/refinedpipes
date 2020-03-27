package com.raoulvdberge.refinedpipes.network.pipe.item;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.message.ItemTransportMessage;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransportProps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ItemPipe extends Pipe {
    private static final Logger LOGGER = LogManager.getLogger(ItemPipe.class);

    private final List<ItemTransport> transports = new ArrayList<>();
    private final List<ItemTransport> transportsToAdd = new ArrayList<>();
    private final List<ItemTransport> transportsToRemove = new ArrayList<>();
    private final ItemPipeType type;

    public ItemPipe(World world, BlockPos pos, ItemPipeType type) {
        super(world, pos);

        this.type = type;
    }

    public void update(World world) {
        super.update(world);

        transports.addAll(transportsToAdd);
        transports.removeAll(transportsToRemove);

        if (!transportsToAdd.isEmpty() || !transportsToRemove.isEmpty()) {
            NetworkManager.get(world).markDirty();
            sendTransportUpdate();
        }

        if (!transports.isEmpty()) {
            NetworkManager.get(world).markDirty();
        }

        transportsToAdd.clear();
        transportsToRemove.clear();

        if (transports.removeIf(t -> t.update(network, this))) {
            NetworkManager.get(world).markDirty();
        }
    }

    public List<ItemTransport> getTransports() {
        return transports;
    }

    public void addTransport(ItemTransport transport) {
        transportsToAdd.add(transport);
    }

    public void removeTransport(ItemTransport transport) {
        transportsToRemove.add(transport);
    }

    public void sendTransportUpdate() {
        List<ItemTransportProps> props = new ArrayList<>();
        for (ItemTransport transport : transports) {
            props.add(transport.createProps(this));
        }

        RefinedPipes.NETWORK.sendInArea(world, pos, 32, new ItemTransportMessage(pos, props));
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag = super.writeToNbt(tag);

        tag.putInt("type", type.ordinal());

        ListNBT transports = new ListNBT();
        for (ItemTransport transport : this.transports) {
            transports.add(transport.writeToNbt(new CompoundNBT()));
        }
        tag.put("transports", transports);

        return tag;
    }

    @Override
    public boolean canFormNetworkWith(Pipe otherPipe) {
        return otherPipe instanceof ItemPipe;
    }

    public static ItemPipe fromNbt(World world, CompoundNBT tag) {
        BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));

        ItemPipeType pipeType = ItemPipeType.values()[tag.getInt("type")];

        ItemPipe pipe = new ItemPipe(world, pos, pipeType);

        ListNBT attch = tag.getList("attch", Constants.NBT.TAG_COMPOUND);
        for (INBT item : attch) {
            CompoundNBT attchTag = (CompoundNBT) item;

            AttachmentType type = AttachmentRegistry.INSTANCE.getType(new ResourceLocation(attchTag.getString("typ")));
            if (type != null) {
                Attachment attachment = type.createFromNbt(attchTag);
                pipe.attachmentManager.setAttachment(attachment.getDirection(), attachment);
            } else {
                LOGGER.warn("Attachment {} no longer exists", attchTag.getString("typ"));
            }
        }

        ListNBT transports = tag.getList("transports", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < transports.size(); ++i) {
            CompoundNBT transportTag = transports.getCompound(i);

            ItemTransport itemTransport = ItemTransport.of(transportTag);
            if (itemTransport != null) {
                pipe.transports.add(itemTransport);
            }
        }

        return pipe;
    }

    public int getMaxTicksInPipe() {
        return type.getMaxTicksInPipe();
    }
}
