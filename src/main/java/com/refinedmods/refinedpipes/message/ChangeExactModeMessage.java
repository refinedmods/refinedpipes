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

public class ChangeExactModeMessage {
    private final BlockPos pos;
    private final Direction direction;
    private final boolean exactMode;

    public ChangeExactModeMessage(BlockPos pos, Direction direction, boolean exactMode) {
        this.pos = pos;
        this.direction = direction;
        this.exactMode = exactMode;
    }

    public static void encode(ChangeExactModeMessage message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
        buf.writeByte(message.direction.ordinal());
        buf.writeBoolean(message.exactMode);
    }

    public static ChangeExactModeMessage decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        Direction direction = DirectionUtil.safeGet(buf.readByte());
        boolean exactMode = buf.readBoolean();

        return new ChangeExactModeMessage(pos, direction, exactMode);
    }

    public static void handle(ChangeExactModeMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TileEntity tile = ctx.get().getSender().world.getTileEntity(message.pos);

            if (tile instanceof PipeTileEntity) {
                Attachment attachment = ((PipeTileEntity) tile).getAttachmentManager().getAttachment(message.direction);

                if (attachment instanceof ExtractorAttachment) {
                    ((ExtractorAttachment) attachment).setExactMode(message.exactMode);

                    NetworkManager.get(tile.getWorld()).markDirty();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
