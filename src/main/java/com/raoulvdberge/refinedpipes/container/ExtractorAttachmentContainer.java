package com.raoulvdberge.refinedpipes.container;

import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.RefinedPipesContainers;
import com.raoulvdberge.refinedpipes.message.ChangeRedstoneModeMessage;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.RedstoneMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class ExtractorAttachmentContainer extends BaseContainer {
    private final BlockPos pos;
    private final Direction dir;

    private RedstoneMode redstoneMode;

    public ExtractorAttachmentContainer(int windowId, PlayerEntity player, BlockPos pos, Direction dir, byte redstoneMode) {
        super(RefinedPipesContainers.EXTRACTOR_ATTACHMENT, windowId);

        addPlayerInventory(player, 8, 111);

        this.pos = pos;
        this.dir = dir;

        this.redstoneMode = RedstoneMode.get(redstoneMode);
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;

        RefinedPipes.NETWORK.sendToServer(new ChangeRedstoneModeMessage(pos, dir, redstoneMode));
    }
}
