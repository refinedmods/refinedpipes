package com.refinedmods.refinedpipes.message;

import com.refinedmods.refinedpipes.blockentity.PipeBlockEntity;
import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.RoutingMode;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeRoutingModeMessage {
    private final BlockPos pos;
    private final Direction direction;
    private final RoutingMode routingMode;

    public ChangeRoutingModeMessage(BlockPos pos, Direction direction, RoutingMode routingMode) {
        this.pos = pos;
        this.direction = direction;
        this.routingMode = routingMode;
    }

    public static void encode(ChangeRoutingModeMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
        buf.writeByte(message.direction.ordinal());
        buf.writeByte(message.routingMode.ordinal());
    }

    public static ChangeRoutingModeMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Direction direction = DirectionUtil.safeGet(buf.readByte());
        RoutingMode redstoneMode = RoutingMode.get(buf.readByte());

        return new ChangeRoutingModeMessage(pos, direction, redstoneMode);
    }

    public static void handle(ChangeRoutingModeMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockEntity blockEntity = ctx.get().getSender().level.getBlockEntity(message.pos);

            if (blockEntity instanceof PipeBlockEntity) {
                Attachment attachment = ((PipeBlockEntity) blockEntity).getAttachmentManager().getAttachment(message.direction);

                if (attachment instanceof ExtractorAttachment) {
                    ((ExtractorAttachment) attachment).setRoutingMode(message.routingMode);

                    NetworkManager.get(blockEntity.getLevel()).setDirty();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
