package com.refinedmods.refinedpipes.message;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.RedstoneMode;
import com.refinedmods.refinedpipes.tile.PipeTileEntity;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeRedstoneModeMessage {
    private final BlockPos pos;
    private final Direction direction;
    private final RedstoneMode redstoneMode;

    public ChangeRedstoneModeMessage(BlockPos pos, Direction direction, RedstoneMode redstoneMode) {
        this.pos = pos;
        this.direction = direction;
        this.redstoneMode = redstoneMode;
    }

    public static void encode(ChangeRedstoneModeMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
        buf.writeByte(message.direction.ordinal());
        buf.writeByte(message.redstoneMode.ordinal());
    }

    public static ChangeRedstoneModeMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Direction direction = DirectionUtil.safeGet(buf.readByte());
        RedstoneMode redstoneMode = RedstoneMode.get(buf.readByte());

        return new ChangeRedstoneModeMessage(pos, direction, redstoneMode);
    }

    public static void handle(ChangeRedstoneModeMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockEntity tile = ctx.get().getSender().level.getBlockEntity(message.pos);

            if (tile instanceof PipeTileEntity) {
                Attachment attachment = ((PipeTileEntity) tile).getAttachmentManager().getAttachment(message.direction);

                if (attachment instanceof ExtractorAttachment) {
                    ((ExtractorAttachment) attachment).setRedstoneMode(message.redstoneMode);

                    NetworkManager.get(tile.getLevel()).setDirty();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
