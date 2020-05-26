package com.refinedmods.refinedpipes.message;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import com.refinedmods.refinedpipes.tile.PipeTileEntity;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static void encode(ChangeStackSizeMessage message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
        buf.writeByte(message.direction.ordinal());
        buf.writeInt(message.stackSize);
    }

    public static ChangeStackSizeMessage decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        Direction direction = DirectionUtil.safeGet(buf.readByte());
        int stackSize = buf.readInt();

        return new ChangeStackSizeMessage(pos, direction, stackSize);
    }

    public static void handle(ChangeStackSizeMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TileEntity tile = ctx.get().getSender().world.getTileEntity(message.pos);

            if (tile instanceof PipeTileEntity) {
                Attachment attachment = ((PipeTileEntity) tile).getAttachmentManager().getAttachment(message.direction);

                if (attachment instanceof ExtractorAttachment) {
                    ((ExtractorAttachment) attachment).setStackSize(message.stackSize);

                    NetworkManager.get(tile.getWorld()).markDirty();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
