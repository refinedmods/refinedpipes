package com.raoulvdberge.refinedpipes.network.pipe.item;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.message.ItemTransportMessage;
import com.raoulvdberge.refinedpipes.network.NetworkManager;
import com.raoulvdberge.refinedpipes.network.item.ItemNetwork;
import com.raoulvdberge.refinedpipes.network.pipe.Pipe;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransport;
import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransportProps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ItemPipe extends Pipe {
    public static final ResourceLocation ID = new ResourceLocation(RefinedPipes.ID, "item");

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
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getNetworkType() {
        return ItemNetwork.TYPE;
    }

    public int getMaxTicksInPipe() {
        return type.getMaxTicksInPipe();
    }
}
