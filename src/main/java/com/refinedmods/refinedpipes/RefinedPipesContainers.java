package com.refinedmods.refinedpipes;

import com.refinedmods.refinedpipes.container.ExtractorAttachmentContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

public class RefinedPipesContainers {
    @ObjectHolder(RefinedPipes.ID + ":extractor_attachment")
    public static final MenuType<ExtractorAttachmentContainer> EXTRACTOR_ATTACHMENT = null;
}
