package com.raoulvdberge.refinedpipes.container;

import com.raoulvdberge.refinedpipes.RefinedPipesContainers;
import net.minecraft.entity.player.PlayerEntity;

public class ExtractorAttachmentContainer extends BaseContainer {
    public ExtractorAttachmentContainer(int windowId, PlayerEntity player) {
        super(RefinedPipesContainers.EXTRACTOR_ATTACHMENT, windowId);

        addPlayerInventory(player, 8, 85);
    }
}
