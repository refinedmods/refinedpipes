package com.raoulvdberge.refinedpipes.container.factory;

import com.raoulvdberge.refinedpipes.container.ExtractorAttachmentContainer;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.*;
import com.raoulvdberge.refinedpipes.util.DirectionUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.IContainerFactory;

public class ExtractorAttachmentContainerFactory implements IContainerFactory<ExtractorAttachmentContainer> {
    @Override
    public ExtractorAttachmentContainer create(int windowId, PlayerInventory inv, PacketBuffer buf) {
        return new ExtractorAttachmentContainer(
            windowId,
            inv.player,
            buf.readBlockPos(),
            DirectionUtil.safeGet(buf.readByte()),
            RedstoneMode.get(buf.readByte()),
            BlacklistWhitelist.get(buf.readByte()),
            RoutingMode.get(buf.readByte()),
            buf.readInt(),
            buf.readBoolean(),
            ExtractorAttachmentType.get(buf.readByte()),
            ExtractorAttachment.createItemFilterInventory(null),
            ExtractorAttachment.createFluidFilterInventory(null),
            buf.readBoolean()
        );
    }
}
