package com.refinedmods.refinedpipes.message;

import com.refinedmods.refinedpipes.network.NetworkManager;
import com.refinedmods.refinedpipes.network.pipe.attachment.Attachment;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.BlacklistWhitelist;
import com.refinedmods.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import com.refinedmods.refinedpipes.tile.PipeTileEntity;
import com.refinedmods.refinedpipes.util.DirectionUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ChangeBlacklistWhitelistMessage {
    private final BlockPos pos;
    private final Direction direction;
    private final BlacklistWhitelist blacklistWhitelist;

    public ChangeBlacklistWhitelistMessage(BlockPos pos, Direction direction, BlacklistWhitelist blacklistWhitelist) {
        this.pos = pos;
        this.direction = direction;
        this.blacklistWhitelist = blacklistWhitelist;
    }

    public static void encode(ChangeBlacklistWhitelistMessage message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
        buf.writeByte(message.direction.ordinal());
        buf.writeByte(message.blacklistWhitelist.ordinal());
    }

    public static ChangeBlacklistWhitelistMessage decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        Direction direction = DirectionUtil.safeGet(buf.readByte());
        BlacklistWhitelist blacklistWhitelist = BlacklistWhitelist.get(buf.readByte());

        return new ChangeBlacklistWhitelistMessage(pos, direction, blacklistWhitelist);
    }

    public static void handle(ChangeBlacklistWhitelistMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TileEntity tile = ctx.get().getSender().level.getBlockEntity(message.pos);

            if (tile instanceof PipeTileEntity) {
                Attachment attachment = ((PipeTileEntity) tile).getAttachmentManager().getAttachment(message.direction);

                if (attachment instanceof ExtractorAttachment) {
                    ((ExtractorAttachment) attachment).setBlacklistWhitelist(message.blacklistWhitelist);

                    NetworkManager.get(tile.getLevel()).setDirty();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
