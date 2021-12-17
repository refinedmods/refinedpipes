package com.refinedmods.refinedpipes.message;

import com.refinedmods.refinedpipes.blockentity.PipeBlockEntity;
import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeExactModeMessage {
    private final BlockPos pos;
    private final Direction direction;
    private final boolean exactMode;

    public ChangeExactModeMessage(BlockPos pos, Direction direction, boolean exactMode) {
        this.pos = pos;
        this.direction = direction;
        this.exactMode = exactMode;
    }

    public static void encode(ChangeExactModeMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
        buf.writeByte(message.direction.ordinal());
        buf.writeBoolean(message.exactMode);
    }

    public static ChangeExactModeMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Direction direction = DirectionUtil.safeGet(buf.readByte());
        boolean exactMode = buf.readBoolean();

        return new ChangeExactModeMessage(pos, direction, exactMode);
    }

    public static void handle(ChangeExactModeMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockEntity blockEntity = ctx.get().getSender().level.getBlockEntity(message.pos);

            if (blockEntity instanceof PipeBlockEntity) {
                Attachment attachment = ((PipeBlockEntity) blockEntity).getAttachmentManager().getAttachment(message.direction);

                if (attachment instanceof ExtractorAttachment) {
                    ((ExtractorAttachment) attachment).setExactMode(message.exactMode);

                    NetworkManager.get(blockEntity.getLevel()).setDirty();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
