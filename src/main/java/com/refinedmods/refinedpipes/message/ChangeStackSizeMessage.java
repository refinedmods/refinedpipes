package com.refinedmods.refinedpipes.message;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import com.refinedmods.refinedpipes.tile.PipeTileEntity;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeStackSizeMessage {
    private final BlockPos pos;
    private final Direction direction;
    private final int stackSize;

    public ChangeStackSizeMessage(BlockPos pos, Direction direction, int stackSize) {
        this.pos = pos;
        this.direction = direction;
        this.stackSize = stackSize;
    }

    public static void encode(ChangeStackSizeMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
        buf.writeByte(message.direction.ordinal());
        buf.writeInt(message.stackSize);
    }

    public static ChangeStackSizeMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Direction direction = DirectionUtil.safeGet(buf.readByte());
        int stackSize = buf.readInt();

        return new ChangeStackSizeMessage(pos, direction, stackSize);
    }

    public static void handle(ChangeStackSizeMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockEntity tile = ctx.get().getSender().level.getBlockEntity(message.pos);

            if (tile instanceof PipeTileEntity) {
                Attachment attachment = ((PipeTileEntity) tile).getAttachmentManager().getAttachment(message.direction);

                if (attachment instanceof ExtractorAttachment) {
                    ((ExtractorAttachment) attachment).setStackSize(message.stackSize);

                    NetworkManager.get(tile.getLevel()).setDirty();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
