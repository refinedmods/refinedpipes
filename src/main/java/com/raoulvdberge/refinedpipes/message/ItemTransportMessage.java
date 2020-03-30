package com.raoulvdberge.refinedpipes.message;

import com.raoulvdberge.refinedpipes.network.pipe.transport.ItemTransportProps;
import com.raoulvdberge.refinedpipes.tile.ItemPipeTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemTransportMessage {
    private final BlockPos pos;
    private final List<ItemTransportProps> props;

    public ItemTransportMessage(BlockPos pos, List<ItemTransportProps> props) {
        this.pos = pos;
        this.props = props;
    }

    public static void encode(ItemTransportMessage message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.props.size());

        message.props.forEach(p -> p.writeToBuffer(buf));
    }

    public static ItemTransportMessage decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        int count = buf.readInt();
        List<ItemTransportProps> props = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            props.add(ItemTransportProps.create(buf));
        }

        return new ItemTransportMessage(pos, props);
    }

    public static void handle(ItemTransportMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TileEntity tile = Minecraft.getInstance().world.getTileEntity(message.pos);

            if (tile instanceof ItemPipeTileEntity) {
                ((ItemPipeTileEntity) tile).setProps(message.props);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
