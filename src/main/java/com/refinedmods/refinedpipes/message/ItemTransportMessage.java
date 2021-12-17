package com.refinedmods.refinedpipes.message;

import com.refinedmods.refinedpipes.blockentity.ItemPipeBlockEntity;
import com.refinedmods.refinedpipes.network.pipe.transport.ItemTransportProps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

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

    public static void encode(ItemTransportMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.props.size());

        message.props.forEach(p -> p.writeToBuffer(buf));
    }

    public static ItemTransportMessage decode(FriendlyByteBuf buf) {
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
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(message.pos);

            if (blockEntity instanceof ItemPipeBlockEntity) {
                ((ItemPipeBlockEntity) blockEntity).setProps(message.props);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
