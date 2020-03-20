package com.raoulvdberge.refinedpipes.network.pipe;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.message.TransportMessage;
import com.raoulvdberge.refinedpipes.network.Network;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.Attachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentManager;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentRegistry;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.AttachmentType;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransportProps;
import net.minecraft.block.BlockState;
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
import java.util.Objects;

public class Pipe {
    private static final Logger LOGGER = LogManager.getLogger(Pipe.class);

    private final World world;
    private final BlockPos pos;
    private final AttachmentManager attachmentManager = new AttachmentManager();

    private Network network;
    private final List<ItemTransport> transports = new ArrayList<>();
    private final List<ItemTransport> transportsToAdd = new ArrayList<>();
    private final List<ItemTransport> transportsToRemove = new ArrayList<>();
    private final PipeType type;

    public Pipe(World world, BlockPos pos, PipeType type) {
        this.world = world;
        this.pos = pos;
        this.type = type;
    }

    public void update(World world) {
        for (Attachment attachment : attachmentManager.getAttachments()) {
            attachment.update(world, network, this);
        }

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

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void joinNetwork(Network network) {
        this.network = network;

        LOGGER.debug(pos + " joined network " + network.getId());

        sendBlockUpdate();
    }

    public void leaveNetwork() {
        LOGGER.debug(pos + " left network " + network.getId());

        this.network = null;

        sendBlockUpdate();
    }

    public void addTransport(ItemTransport transport) {
        transportsToAdd.add(transport);
    }

    public void removeTransport(ItemTransport transport) {
        transportsToRemove.add(transport);
    }

    public void sendBlockUpdate() {
        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 1 | 2);
    }

    public void sendTransportUpdate() {
        List<ItemTransportProps> props = new ArrayList<>();
        for (ItemTransport transport : transports) {
            props.add(transport.createProps(this));
        }

        RefinedPipes.NETWORK.sendInArea(world, pos, 32, new TransportMessage(pos, props));
    }

    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putLong("pos", pos.toLong());

        tag.putInt("type", type.ordinal());

        ListNBT attch = new ListNBT();
        attachmentManager.getAttachments().forEach(a -> {
            CompoundNBT attchTag = new CompoundNBT();
            attchTag.putString("typ", a.getType().getId().toString());
            attch.add(a.writeToNbt(attchTag));
        });
        tag.put("attch", attch);

        ListNBT transports = new ListNBT();
        for (ItemTransport transport : this.transports) {
            transports.add(transport.writeToNbt(new CompoundNBT()));
        }
        tag.put("transports", transports);

        return tag;
    }

    public static Pipe fromNbt(World world, CompoundNBT tag) {
        BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));

        PipeType pipeType = PipeType.values()[tag.getInt("type")];

        Pipe pipe = new Pipe(world, pos, pipeType);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pipe pipe = (Pipe) o;
        return world.equals(pipe.world) &&
            pos.equals(pipe.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, pos);
    }
}
